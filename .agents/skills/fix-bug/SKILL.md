---
name: fix-bug
description: Fix a labeled Petstore bug from a GitHub issue and open a draft PR.
---

# Fix Bug Skill

Use this skill when an issue receives the `oh:fix-bug` label.

## Goals

1. Read the issue carefully and reproduce the bug from the issue body.
2. Make the smallest safe fix.
3. Add regression coverage whenever practical.
4. Open a draft PR back to the default branch.
5. Comment on the issue with the PR link, test results, and what changed.

## Repository-specific guidance

- Relevant Java code is primarily under `src/main/java/io/swagger/petstore/controller/`.
- Favor targeted tests under `src/test/java/` over large integration scaffolding.
- Useful examples from the current codebase include:
  - `PetController.findPetsByStatus(...)` currently sends a `Pets not found` notifier even on success.
  - delete handlers such as `deleteUser(...)` and `deleteOrder(...)` return empty entities on successful deletion.
- Verify with `mvn --no-transfer-progress -B test` or the closest smaller command you can justify.

## PR expectations

- Create a draft PR.
- Include `Closes #<issue_number>` when appropriate.
- Summarize the root cause, fix, and verification.
- Add a short AI disclosure note in any GitHub-facing comment or PR body.
