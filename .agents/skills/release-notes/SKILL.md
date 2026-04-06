---
name: release-notes
description: Prepare release notes for the Petstore demo using git history and existing release scripts.
---

# Release Notes Skill

Use this skill for the manual release-notes workflow.

## Goals

1. Inspect git history, tags, and the existing `CI/releaseNotes.py` helper.
2. Generate polished release notes for the requested release tag or version.
3. Prefer a reviewable artifact, such as a markdown file or draft PR, unless a draft GitHub release is clearly the better fit.

## Repository-specific guidance

- Existing release automation already lives in:
  - `CI/releaseNotes.py`
  - `CI/prepare-release.sh`
  - `.github/workflows/release.yml`
- Organize notes around user-visible fixes, API behavior changes, developer workflow updates, and dependency updates.
- Keep the output clean and customer-facing.

## Expected deliverables

- A release-notes artifact based on the workflow inputs.
- A concise summary of the comparison range used.
- Clear callouts for any breaking or potentially risky change.
- A short AI disclosure note in any GitHub-facing content.
