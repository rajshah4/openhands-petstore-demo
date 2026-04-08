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

## Suggested demo story

A clean end-to-end presenter flow:

1. Show the public repository and the OpenHands workflows
2. Open a labeled bug issue with `oh:fix-bug`
3. Show the acknowledgement comment and OpenHands conversation link
4. Show the resulting draft bug-fix PR
5. Open a second labeled issue with `oh:add-ci-check`
6. Show the resulting draft CI-check PR
7. Trigger `OpenHands Expand Test Coverage`
8. Trigger `OpenHands Release Notes`
9. Open or update a pull request to show `OpenHands PR Code Review`
10. Optionally show the dependency-management workflow opening its own PR

## Good built-in demo targets

These are visible in the Petstore controllers and work well as issue prompts:

- `PetController.findPetsByStatus(...)` sends a false error notifier even on successful responses
- `UserController.deleteUser(...)` returns a null entity on successful deletion
- `OrderController.deleteOrder(...)` returns a null entity on successful deletion

## Sample draft issues to create

You are creating the GitHub issues during the demo. The simplest path is to use the built-in issue templates, but you can also copy-paste drafts like these.

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
- `#5` — CI smoke-test workflow
- `#7` — release notes
- `#8` — dependency management
- `#9` — bug fix
- `#10` — test expansion

These artifacts are useful to keep around because they show the different OpenHands-generated outcomes from the live test runs.

## Optional cleanup after a demo

If you want to reset the repo after presenting:
- close or merge the generated demo PRs
- delete the demo branches you no longer need
- create fresh labeled issues for the next run so the workflow history stays easy to explain
