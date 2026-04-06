---
name: create-ci-check
description: Add a new CI check to the Petstore repo from a labeled GitHub issue.
---

# Create CI Check Skill

Use this skill when an issue receives the `oh:add-ci-check` label.

## Goals

1. Treat the issue body as the specification for a new CI check.
2. Add the smallest useful workflow and any required helper scripts or tests.
3. Avoid duplicating existing workflows.
4. Open a draft PR and comment on the issue with the result.

## Repository-specific guidance

Existing checks already cover:
- Maven build on `master`
- Maven build on pull requests
- Dependency review
- CodeQL
- Release and Docker publishing workflows

A strong new demo check for this repo would usually be one of:
- Boot Jetty and smoke-test `/api/v3/openapi.json`
- Run focused API checks against a controller behavior the issue describes
- Validate generated OpenAPI or static UI assets

## Output expectations

- Add or update `.github/workflows/<name>.yml`
- Keep runtime fast and permissions minimal
- Include verification notes in the PR body or issue comment
- Add a short AI disclosure note in any GitHub-facing comment or PR body
