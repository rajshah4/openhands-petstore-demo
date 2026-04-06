# Release Notes — demo-v0.1.0

**Release tag:** `demo-v0.1.0`  
**Date:** 2026-04-06  
**Comparison range:** upstream `1.0.27` base (`8f0dd28`) → `master` (`c287a07`)  
**Branch:** `master`

---

## Overview

`demo-v0.1.0` is the **first release of the OpenHands Petstore Demo**, a GitHub-native showcase
that layers OpenHands Cloud automation on top of the [swagger-api/swagger-petstore](https://github.com/swagger-api/swagger-petstore)
reference application. This release establishes the full demo scaffolding and picks up the
upstream petstore `1.0.27` changes as its base.

---

## 🤖 OpenHands Demo Infrastructure (new in this release)

### Agent Skills

Six reusable OpenHands skills have been added under `.agents/skills/`. Each skill provides
task-specific instructions that an OpenHands Cloud conversation executes when triggered:

| Skill | Trigger | Description |
|---|---|---|
| `code-review` | Pull request opened / updated | Reviews PR diffs for correctness, API behavior, and test coverage |
| `fix-bug` | Issue labeled `oh:fix-bug` | Reads issue, reproduces bug, applies minimal fix, opens draft PR |
| `create-ci-check` | Issue labeled `oh:add-ci-check` | Adds a focused, fast GitHub Actions check workflow |
| `expand-test-coverage` | Manual workflow dispatch | Identifies coverage gaps, adds JUnit tests |
| `dependency-management` | Manual or scheduled workflow | Audits and proposes safe dependency updates |
| `release-notes` | Manual workflow dispatch | Generates polished, customer-facing release notes |

### GitHub Actions Workflows

Five new OpenHands-specific workflows were added under `.github/workflows/`:

- **`openhands-issue-router.yml`** — Dispatches an OpenHands Cloud conversation when an issue
  is labeled `oh:fix-bug` or `oh:add-ci-check`.
- **`openhands-pr-code-review.yml`** — Triggers the `code-review` skill on `pull_request_target`
  events.
- **`openhands-dependency-management.yml`** — Manual/scheduled workflow for dependency audits.
- **`openhands-expand-test-coverage.yml`** — Manual workflow for test coverage expansion.
- **`openhands-release-notes.yml`** — Manual workflow that produces versioned release notes
  (the workflow that generated this file).

### Dispatcher & Cloud Client

- **`.github/openhands/dispatch.py`** — Lightweight Python dispatcher that reads the workflow
  event context, selects the appropriate skill, and starts an OpenHands Cloud V1
  `app-conversations` API call.
- **`.github/openhands/cloud_client.py`** — HTTP client wrapper used by the dispatcher;
  improved conversation link construction and smoke-test reliability in follow-up commits.

### Issue Templates

Two GitHub issue templates were added for demo self-service:

- `.github/ISSUE_TEMPLATE/fix-bug-demo.md` — Bug report form that pre-populates the
  `oh:fix-bug` label.
- `.github/ISSUE_TEMPLATE/add-ci-check-demo.md` — CI check request form that pre-populates
  the `oh:add-ci-check` label.

### Helper Scripts

- **`scripts/create_demo_issue.py`** — Creates demo GitHub issues (supports `--dry-run`).
  Includes AI-generated-content disclosure on all posted content.
- **`scripts/test_cloud_trigger.py`** — Smoke-tests an OpenHands Cloud trigger end-to-end.

### Repository Memory

- **`AGENTS.md`** — Persistent repository memory file. Provides OpenHands with project
  identity, build commands, useful code hotspots, known bugs, and CI safety notes across all
  conversations.

### Documentation

- **`README.md`** was substantially updated to document the demo architecture, workflows,
  setup prerequisites, and usage instructions.

---

## ⚠️ Upstream Petstore Changes (inherited from v1.0.27)

These changes come from the upstream `swagger-api/swagger-petstore` project and are included
in this fork at the `1.0.27` baseline.

### Potentially Breaking — Query Parameters Now Required

| Endpoint | Parameter | Change |
|---|---|---|
| `GET /pet/findByStatus` | `status` | Changed from optional to **required** |
| `GET /pet/findByTags` | `tags` | Changed from optional to **required** |

Clients that call these endpoints without the respective query parameter will now receive a
validation error rather than an empty result set. Update API consumers accordingly.

> Upstream commit: `9fb97b1` — _Change status/tag to required param for findbystatus/findbytag endpoints_

### Developer Workflow — Maven Publishing Modernized

The Maven publishing setup was migrated from OSSRH (Sonatype legacy) to the new Maven Central
publishing portal. This affects the CI `release.yml` workflow and `pom.xml` distribution
management configuration. No impact on runtime API behavior.

> Upstream commit: `0f0761b` — _modernize POM and CI (OSSRH→Central)_

---

## 📋 Open Draft PRs (pending review, not included in this release)

The following draft PRs were opened by OpenHands as part of demo runs and are awaiting
human review:

- **[#4](https://github.com/rajshah4/openhands-petstore-demo/pull/4)** —
  `fix: correct spurious error notifier and null entity returns in controllers`  
  Fixes three controller bugs: spurious `notifier.notify()` call on success in
  `PetController.findPetsByStatus`, and null entity returns in `UserController.deleteUser`
  and `OrderController.deleteOrder`.

- **[#5](https://github.com/rajshah4/openhands-petstore-demo/pull/5)** —
  `ci: add API smoke-test workflow (Jetty + openapi.json probe)`  
  Adds a lightweight end-to-end smoke test (Jetty server start → openapi.json probe →
  `/pet/findByStatus` probe) that runs on every push and pull request.

---

## 🔍 Comparison Summary

| Category | Details |
|---|---|
| Base commit | `8f0dd28` (upstream petstore `1.0.27` release) |
| Head commit | `c287a07` (current `master`) |
| Demo-specific commits | 3 (`5b9f00d`, `0c8e1f5`, `c287a07`) |
| Files added (demo) | 21 files (skills, workflows, scripts, templates, docs) |
| Upstream base version | `swagger-api/swagger-petstore` `1.0.27` |

---

## ✅ Verification

The Maven build passes cleanly at this revision:

```
mvn --no-transfer-progress -B test
```

No breaking changes are introduced by the demo scaffold itself. The only breaking change
is the upstream API spec change to `findByStatus`/`findByTags` parameter requirements,
documented above.

---

_These release notes were generated by OpenHands on behalf of the repository operator
(workflow run [#24039036602](https://github.com/rajshah4/openhands-petstore-demo/actions/runs/24039036602),
triggered by @rajshah4)._
