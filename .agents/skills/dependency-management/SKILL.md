---
name: dependency-management
description: Audit and safely update Petstore dependencies with a reviewable PR.
---

# Dependency Management Skill

Use this skill for the manual or scheduled dependency-management workflow.

## Goals

1. Audit `pom.xml`, GitHub workflow actions, and any Python helper dependencies used by the OpenHands demo.
2. Prefer safe patch and minor updates.
3. Avoid broad refactors or framework migrations.
4. Open a draft PR with a compact summary of what changed and why.

## Repository-specific guidance

- Main dependency surface is `pom.xml`.
- Existing workflow actions live under `.github/workflows/`.
- Existing release helper scripts live in `CI/`.
- Verify updates with `mvn --no-transfer-progress -B test` when feasible.
- If a dependency appears risky or major-version-only, explain it and defer it instead of forcing the upgrade.

## Expected deliverables

- Minimal version bumps with rationale.
- Updated workflow action versions when clearly beneficial.
- Draft PR summarizing:
  - updated dependencies
  - deferred upgrades and why
  - verification performed
- Add a short AI disclosure note in any GitHub-facing comment or PR body.
