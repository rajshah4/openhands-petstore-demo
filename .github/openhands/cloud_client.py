from __future__ import annotations

import os
import time
from typing import Any

import httpx

DEFAULT_BASE_URL = os.getenv("OPENHANDS_BASE_URL", "https://app.all-hands.dev").rstrip("/")
GITHUB_API_URL = "https://api.github.com"


class OpenHandsCloudClient:
    def __init__(self, api_key: str, base_url: str = DEFAULT_BASE_URL):
        self.api_key = api_key
        self.base_url = base_url.rstrip("/")
        self.headers = {
            "Authorization": f"Bearer {api_key}",
            "X-Access-Token": api_key,
            "Content-Type": "application/json",
        }

    def create_conversation_start_task(
        self,
        *,
        initial_user_msg: str,
        repository: str,
        selected_branch: str,
        title: str | None = None,
    ) -> dict[str, Any]:
        payload: dict[str, Any] = {
            "initial_message": {
                "role": "user",
                "content": [{"type": "text", "text": initial_user_msg}],
                "run": True,
            },
            "selected_repository": repository,
            "selected_branch": selected_branch,
        }
        if title:
            payload["title"] = title

        response = httpx.post(
            f"{self.base_url}/api/v1/app-conversations",
            headers=self.headers,
            json=payload,
            timeout=30,
        )
        response.raise_for_status()
        return response.json()

    def get_start_task(self, task_id: str) -> dict[str, Any] | None:
        response = httpx.get(
            f"{self.base_url}/api/v1/app-conversations/start-tasks",
            headers=self.headers,
            params={"ids": task_id},
            timeout=30,
        )
        response.raise_for_status()
        tasks = response.json()
        return tasks[0] if tasks else None

    def wait_for_ready(
        self,
        task_id: str,
        *,
        timeout_s: int = 180,
        poll_interval_s: int = 2,
    ) -> dict[str, Any]:
        start_time = time.time()
        while True:
            if time.time() - start_time > timeout_s:
                raise TimeoutError(f"OpenHands conversation did not start within {timeout_s}s")

            task = self.get_start_task(task_id)
            if not task:
                raise RuntimeError(f"OpenHands start task disappeared: {task_id}")

            status = str(task.get("status", "unknown")).upper()
            if status == "READY":
                return task
            if status == "ERROR":
                detail = task.get("detail") or "OpenHands failed to initialize the conversation"
                raise RuntimeError(detail)

            time.sleep(poll_interval_s)

    def get_conversation(self, conversation_id: str) -> dict[str, Any] | None:
        response = httpx.get(
            f"{self.base_url}/api/v1/app-conversations",
            headers=self.headers,
            params={"ids": conversation_id},
            timeout=30,
        )
        response.raise_for_status()
        conversations = response.json()
        return conversations[0] if conversations else None

    def start_conversation(
        self,
        *,
        initial_user_msg: str,
        repository: str,
        selected_branch: str,
        title: str | None = None,
        max_attempts: int = 3,
    ) -> dict[str, Any]:
        last_error: Exception | None = None

        for attempt in range(1, max_attempts + 1):
            try:
                start_task = self.create_conversation_start_task(
                    initial_user_msg=initial_user_msg,
                    repository=repository,
                    selected_branch=selected_branch,
                    title=title,
                )
                task_id = start_task.get("id")
                if not task_id:
                    raise RuntimeError("OpenHands start task returned no id")

                ready_task = self.wait_for_ready(task_id)
                conversation_id = ready_task.get("app_conversation_id")
                if not conversation_id:
                    raise RuntimeError("OpenHands start task returned no app_conversation_id")

                conversation = self.get_conversation(conversation_id) or {}
                return {
                    "start_task": start_task,
                    "ready_task": ready_task,
                    "conversation": conversation,
                    "conversation_id": conversation_id,
                    "conversation_url": conversation.get(
                        "conversation_url",
                        f"{self.base_url}/conversations/{conversation_id}",
                    ),
                }
            except Exception as exc:  # noqa: BLE001
                last_error = exc
                if attempt == max_attempts:
                    raise
                time.sleep(attempt * 3)

        raise RuntimeError(f"OpenHands conversation failed to start: {last_error}")


class GitHubClient:
    def __init__(self, token: str):
        self.headers = {
            "Authorization": f"Bearer {token}",
            "Accept": "application/vnd.github+json",
            "Content-Type": "application/json",
        }

    def create_issue_comment(self, repo: str, number: int, body: str) -> dict[str, Any]:
        response = httpx.post(
            f"{GITHUB_API_URL}/repos/{repo}/issues/{number}/comments",
            headers=self.headers,
            json={"body": body},
            timeout=30,
        )
        response.raise_for_status()
        return response.json()
