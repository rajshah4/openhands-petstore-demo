#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import os
import subprocess
import time

LABELS = ["oh:fix-bug", "oh:add-ci-check"]


def run(args: list[str]) -> subprocess.CompletedProcess[str]:
    return subprocess.run(args, capture_output=True, text=True, check=False)


def gh_json(args: list[str]) -> object:
    result = run(["gh", *args])
    if result.returncode != 0:
        raise RuntimeError(result.stderr.strip() or "gh command failed")
    return json.loads(result.stdout)


def get_repo() -> str:
    configured = os.getenv("GITHUB_REPO")
    if configured:
        return configured

    result = run(["git", "remote", "get-url", "origin"])
    if result.returncode != 0:
        raise RuntimeError("Could not determine repository from git remote")

    url = result.stdout.strip()
    if url.startswith("git@github.com:"):
        return url.replace("git@github.com:", "").removesuffix(".git")
    if "github.com/" in url:
        return url.split("github.com/")[-1].removesuffix(".git")
    raise RuntimeError(f"Unsupported remote URL: {url}")


def create_issue(repo: str, label: str) -> tuple[int, str]:
    title = f"[cloud-trigger-smoke] verify {label}"
    body = f"""## Cloud Trigger Smoke Test

This disposable issue verifies that the `{label}` label triggers the GitHub Action and starts an OpenHands Cloud conversation.

Expected outcome:
- A workflow acknowledgement comment appears on the issue.
- The comment includes an OpenHands conversation URL.
"""

    result = run([
        "gh", "issue", "create",
        "--repo", repo,
        "--title", title,
        "--body", body,
        "--label", label,
    ])
    if result.returncode != 0:
        raise RuntimeError(result.stderr.strip() or "failed to create issue")

    issue_url = result.stdout.strip()
    issue = gh_json(["issue", "view", issue_url, "--repo", repo, "--json", "number,url"])
    return int(issue["number"]), str(issue["url"])


def fetch_comments(repo: str, number: int) -> list[dict]:
    comments = gh_json(["api", f"repos/{repo}/issues/{number}/comments"])
    if not isinstance(comments, list):
        raise RuntimeError("unexpected comments payload")
    return comments


def close_issue(repo: str, number: int) -> None:
    result = run([
        "gh", "issue", "close", str(number),
        "--repo", repo,
        "--comment", "Closing disposable OpenHands trigger smoke test issue.",
    ])
    if result.returncode != 0:
        raise RuntimeError(result.stderr.strip() or "failed to close issue")


def classify_comment(comment: dict) -> tuple[str, str] | None:
    body = str(comment.get("body") or "")
    lowered = body.lower()
    if "openhands started" in lowered and "app.all-hands.dev" in lowered:
        return ("ok", body)
    if "failed to initialize" in lowered or "did not start" in lowered:
        return ("failed", body)
    return None


def wait_for_comment(repo: str, number: int, timeout_seconds: int, poll_seconds: int) -> tuple[str, str]:
    deadline = time.time() + timeout_seconds
    while time.time() < deadline:
        for comment in fetch_comments(repo, number):
            classified = classify_comment(comment)
            if classified:
                return classified
        time.sleep(poll_seconds)
    return ("timeout", "No OpenHands acknowledgement comment was observed before timeout.")


def main() -> int:
    parser = argparse.ArgumentParser(description="Smoke test issue-label to OpenHands Cloud trigger")
    parser.add_argument("--repo", default=None, help="GitHub repo in owner/name format")
    parser.add_argument("--label", choices=LABELS, default="oh:fix-bug")
    parser.add_argument("--timeout", type=int, default=120)
    parser.add_argument("--poll-interval", type=int, default=5)
    parser.add_argument("--keep-issue", action="store_true")
    args = parser.parse_args()

    repo = args.repo or get_repo()
    print(f"Creating disposable issue in {repo} with label {args.label}...")
    number, url = create_issue(repo, args.label)
    print(f"Created issue #{number}: {url}")
    print("Waiting for workflow acknowledgement comment...")

    exit_code = 0
    try:
        status, detail = wait_for_comment(repo, number, args.timeout, args.poll_interval)
        if status == "ok":
            print("PASS OpenHands workflow comment detected.")
            print(detail)
        else:
            print(f"FAIL Trigger smoke test ended with status: {status}")
            print(detail)
            exit_code = 1
    finally:
        if args.keep_issue:
            print(f"Keeping issue #{number} open for inspection.")
        else:
            print(f"Closing disposable issue #{number}...")
            close_issue(repo, number)
            print("Issue closed.")

    return exit_code


if __name__ == "__main__":
    raise SystemExit(main())
