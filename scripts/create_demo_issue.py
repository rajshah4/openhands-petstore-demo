#!/usr/bin/env python3
from __future__ import annotations

import argparse
import os
import subprocess
import sys
from datetime import datetime, timezone

LABELS = {
    "bug": "oh:fix-bug",
    "ci": "oh:add-ci-check",
}

SCENARIOS = {
    "bug": {
        "false-positive-notifier": {
            "title": "[bug] Stop false error notifications in findPetsByStatus",
            "body": """## Problem
`PetController.findPetsByStatus(...)` appears to notify an error even when matching pets are returned successfully.

## Where it shows up
- File or endpoint: `src/main/java/io/swagger/petstore/controller/PetController.java`
- Current behavior: successful `findPetsByStatus` requests still trigger a `Pets not found` notifier path
- Expected behavior: only missing or invalid requests should trigger error notifications

## Reproduction steps
1. Inspect `PetController.findPetsByStatus(...)`
2. Follow the success path when `petByStatus` is not null
3. Confirm the notifier is still called with a false `Pets not found` error

## Acceptance criteria
- [ ] Remove the false-positive notifier call on successful responses
- [ ] Add a regression test under `src/test/java/`
- [ ] Open a draft PR summarizing the fix
""",
        },
        "delete-user-message": {
            "title": "[bug] Return an explicit success message when deleting a user",
            "body": """## Problem
`UserController.deleteUser(...)` returns an empty entity on successful deletion.

## Where it shows up
- File or endpoint: `src/main/java/io/swagger/petstore/controller/UserController.java`
- Current behavior: successful deletes return a null entity
- Expected behavior: successful deletes return a clear confirmation message

## Acceptance criteria
- [ ] Return a user-friendly success message after deletion
- [ ] Add regression coverage if practical
- [ ] Open a draft PR summarizing the change
""",
        },
    },
    "ci": {
        "api-smoke-check": {
            "title": "[ci] Add an API smoke-test workflow for Petstore",
            "body": """## Goal
Add a fast CI check that boots the Petstore app and verifies a few live API endpoints.

## Why this check matters
- Catch broken startup or routing changes that `mvn install` alone may miss
- Validate the generated OpenAPI endpoint for demo readiness

## Suggested implementation
- Workflow name: `petstore-smoke.yml`
- Commands to run:
  - `mvn --no-transfer-progress -B package jetty:run &`
  - wait for startup
  - `curl --fail http://127.0.0.1:8080/api/v3/openapi.json`
- Expected pass criteria:
  - app starts successfully
  - OpenAPI JSON responds with HTTP 200

## Acceptance criteria
- [ ] New focused CI workflow
- [ ] No duplication of the existing Maven build jobs
- [ ] Draft PR linked back to this issue
""",
        }
    },
}


def run(args: list[str]) -> subprocess.CompletedProcess[str]:
    return subprocess.run(args, capture_output=True, text=True, check=False)


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


def create_issue(kind: str, scenario: str, add_label: bool, dry_run: bool) -> int:
    repo = get_repo()
    config = SCENARIOS[kind][scenario]
    body = config["body"] + f"\n\n_Requested at {datetime.now(timezone.utc).isoformat()} by create_demo_issue.py._\n"

    print(f"Repository: {repo}")
    print(f"Kind: {kind}")
    print(f"Scenario: {scenario}")
    print(f"Label: {LABELS[kind] if add_label else '(none)'}")
    print(f"Title: {config['title']}")

    if dry_run:
        print("\n--- issue body ---")
        print(body)
        print("--- end issue body ---")
        return 0

    cmd = [
        "gh", "issue", "create",
        "--repo", repo,
        "--title", config["title"],
        "--body", body,
    ]
    if add_label:
        cmd.extend(["--label", LABELS[kind]])

    result = run(cmd)
    if result.returncode != 0:
        print(result.stderr.strip() or "Failed to create issue", file=sys.stderr)
        return 1

    print(f"\nCreated issue: {result.stdout.strip()}")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description="Create demo issues for the OpenHands Petstore repo")
    parser.add_argument("--kind", choices=sorted(SCENARIOS), default="bug")
    parser.add_argument("--scenario", help="Scenario key to use")
    parser.add_argument("--no-label", action="store_true", help="Create the issue without the triggering label")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    scenario = args.scenario or sorted(SCENARIOS[args.kind])[0]
    if scenario not in SCENARIOS[args.kind]:
        print(f"Unknown scenario '{scenario}' for kind '{args.kind}'", file=sys.stderr)
        print(f"Available: {sorted(SCENARIOS[args.kind])}", file=sys.stderr)
        return 1

    return create_issue(args.kind, scenario, not args.no_label, args.dry_run)


if __name__ == "__main__":
    raise SystemExit(main())
