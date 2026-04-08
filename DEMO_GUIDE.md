# OpenHands Petstore Demo Guide

This guide is for presenters and demoers.

If you want to reproduce the functionality, configure the repo, or run the workflows yourself, use [`README.md`](./README.md) instead.

## Live-test status

Yes — this demo has been validated live in GitHub and OpenHands Cloud.

Validated flows:
- PR code review
- tagged issue -> OpenHands bug-fix run (`oh:fix-bug`) -> draft PR
- tagged issue -> OpenHands CI-check run (`oh:add-ci-check`) -> draft PR
- manual dependency management
- manual release notes
- manual test expansion

The live validation produced real GitHub comments, real OpenHands conversation links, and draft pull requests.

## Presenter prerequisites

Before a live demo, confirm these repo settings:
- repository is public
- `OPENHANDS_API_KEY` exists in GitHub Actions secrets
- labels exist:
  - `oh:fix-bug`
  - `oh:add-ci-check`
- Dependency Graph is enabled so inherited `Dependency Review` checks stay green

## Skill mix used in this demo

This demo intentionally uses a mix of official OpenHands skills and custom repo-local skills.

- Official OpenHands skills, vendored locally under `.agents/skills/`:
  - `code-review`
  - `releasenotes`
- Custom repo-local demo skills:
  - `fix-bug`
  - `create-ci-check`
  - `dependency-management`
  - `expand-test-coverage`

That lets the demo show both reusable OpenHands platform capabilities and repo-specific GitHub-native automations.

## What is currently safe to show

Validated artifacts as of the latest review:
- `#9` — best bug-fix artifact to show; build check passes
- `#10` — strongest green PR artifact; build, CodeQL, and dependency review pass
- `#14` — freshest release-notes artifact using the official `releasenotes` skill; checks passed
- `#8` — dependency-management artifact; rerun checks passed
- `#6` — closed historical PR-review smoke test; useful for explaining the review flow

Artifacts to treat as optional or cautionary:
- `#5` — good example of OpenHands creating a CI-check PR, but the generated `API Smoke Test` currently fails and is better framed as a guardrails example than a clean hero demo
- `#12` — newer bug-fix draft PR created from a fresh issue; it currently fails `Build Test PR`, so do not use it as the primary success-path example


## Suggested demo story

Use one of these two paths depending on how much live risk you want.

### Safer presenter path (recommended)

This is the path to use if you want a dependable walkthrough with already-validated artifacts.

1. Show the public repository and the OpenHands workflows
2. Open representative bug-fix PR `#9` to show labeled issue -> OpenHands conversation -> draft PR
3. Open representative test-expansion PR `#10` to show a larger coding change with green checks
4. Open representative release-notes PR `#14` to show the official `releasenotes` skill producing a fresh reviewable artifact
5. Open representative dependency-management PR `#8`
6. Open closed PR `#6` if you want to show the PR code-review flow history
7. Mention that draft PRs intentionally skip `OpenHands PR Code Review` until they are marked ready for review

### Optional live-run path

Use this only if you are comfortable with draft PRs being non-deterministic and not always ending with green checks.

1. Create a labeled issue with `oh:fix-bug` or `oh:add-ci-check`
2. Show the acknowledgement comment and OpenHands conversation link
3. Show the resulting draft PR
4. Explain that the draft PR still has to pass normal CI and review gates like any human-authored change

## Good built-in demo targets

These are visible in the Petstore controllers and work well as issue prompts:

- `PetController.findPetsByStatus(...)` sends a false error notifier even on successful responses
- `UserController.deleteUser(...)` returns a null entity on successful deletion
- `OrderController.deleteOrder(...)` returns a null entity on successful deletion

## Sample draft issues to create

You are creating the GitHub issues during the demo. The simplest path is to use the built-in issue templates, but you can also copy-paste drafts like these.

Important: these issue drafts are good for showing issue routing, acknowledgement comments, conversation creation, and draft PR generation. They are not guaranteed to produce a fully green PR on every fresh run, so use the safer presenter path above if you need a predictable demo.

### Sample bug issue (`oh:fix-bug`)

**Title**
```text
[bug] False-positive error notifier in PetController.findPetsByStatus
```

**Label**
```text
oh:fix-bug
```

**Body**
```md
## Problem
`PetController.findPetsByStatus(...)` appears to trigger an error notifier even when the request succeeds and pets are returned.

## Where it shows up
- File or endpoint: `src/main/java/io/swagger/petstore/controller/PetController.java`
- Current behavior: a successful status lookup can still fire a `Pets not found` error notification
- Expected behavior: successful lookups should return the pets without sending an error notification

## Reproduction steps
1. Inspect `PetController.findPetsByStatus(...)`
2. Call the endpoint with a valid status such as `available`
3. Confirm the success path should not notify an error

## Acceptance criteria
- [ ] Minimal code fix
- [ ] Regression test or clear explanation if testing is blocked
- [ ] Draft PR linked back to this issue

## Notes for OpenHands
Please keep the fix focused and add a regression test if practical.
```

### Sample CI issue (`oh:add-ci-check`)

**Title**
```text
[ci] Add a lightweight API smoke-test workflow
```

**Label**
```text
oh:add-ci-check
```

**Body**
```md
## Goal
Add a lightweight CI workflow that proves the Petstore app can start and serve its OpenAPI document.

## Why this check matters
- It should catch runtime failures that a compile-only build misses
- It should run on pull requests
- It should stay fast and targeted

## Suggested implementation
- Workflow name: `API Smoke Test`
- Commands to run: build the app, start Jetty, wait for readiness, and curl `/api/v3/openapi.json`
- Expected pass criteria: the endpoint returns HTTP 200 and contains an `openapi` field

## Acceptance criteria
- [ ] New workflow or targeted CI update
- [ ] No overlap with existing Maven, CodeQL, or dependency-review jobs
- [ ] Draft PR linked back to this issue
```

## Representative live artifacts

Current representative demo PRs in the public repo:
- `#5` — CI smoke-test workflow creation (use as a guardrails example, not the cleanest hero path)
- `#8` — dependency management
- `#9` — bug fix
- `#10` — test expansion
- `#12` — fresh bug-fix rerun that shows CI catching a bad draft proposal
- `#14` — fresh release-notes artifact produced after switching to the official `releasenotes` skill

Historical artifact still useful for storytelling:
- `#7` — earlier release-notes artifact from the pre-refactor validation pass

These artifacts are useful to keep around because they show different OpenHands-generated outcomes from the live test runs: both successful proposals and proposals that still need human review and CI feedback.

## Optional cleanup after a demo

If you want to reset the repo after presenting:
- close or merge the generated demo PRs
- delete the demo branches you no longer need
- create fresh labeled issues for the next run so the workflow history stays easy to explain
