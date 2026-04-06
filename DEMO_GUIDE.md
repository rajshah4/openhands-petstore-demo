# OpenHands Petstore Demo Guide

This guide is for presenters and demoers.

If you want to reproduce the functionality, configure the repo, or run the workflows yourself, use [`README.md`](./README.md) instead.

## Live-test status

Yes — this demo has been validated live in GitHub and OpenHands Cloud.

Validated flows:
- PR code review
- tagged issue -> bug fix (`oh:fix-bug`)
- tagged issue -> new CI check (`oh:add-ci-check`)
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
4. Show the resulting draft PR for the bug fix
5. Open a second labeled issue with `oh:add-ci-check`
6. Show the resulting CI workflow PR
7. Trigger `OpenHands Expand Test Coverage`
8. Trigger `OpenHands Release Notes`
9. Open or update a pull request to show `OpenHands PR Code Review`
10. Optionally show the dependency-management workflow opening its own PR

## Good built-in demo targets

These are visible in the Petstore controllers and work well as issue prompts:

- `PetController.findPetsByStatus(...)` sends a false error notifier even on successful responses
- `UserController.deleteUser(...)` returns a null entity on successful deletion
- `OrderController.deleteOrder(...)` returns a null entity on successful deletion

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
