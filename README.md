# OpenHands Petstore Demo

This repository turns the upstream Swagger Petstore sample into a GitHub-native OpenHands Cloud demo.

It is intended to live in a real GitHub repository so the workflows can be exercised end to end.

It showcases these flows:

1. PR code review using an OpenHands skill
2. Dependency management from a manual or scheduled workflow
3. Release notes generation from a manual workflow
4. Tagged GitHub issue -> bug fix (`oh:fix-bug`)
5. Tagged GitHub issue -> new CI check (`oh:add-ci-check`)
6. Manual test coverage expansion using a dedicated skill

The trigger pattern follows the latest GitHub-native approach from your `demo-spec-driven`, `dailyme`, `openhands-sre`, and related demos:

- GitHub Actions starts a small Python dispatcher
- the dispatcher creates an OpenHands Cloud conversation
- repo-local skills in `.agents/skills/` tell OpenHands what to do

## Demo architecture

```text
GitHub event or workflow_dispatch
  -> .github/openhands/dispatch.py
  -> OpenHands Cloud app conversation
  -> repo-local skill prompt
  -> OpenHands comments, branches, and opens PRs
```

## Added OpenHands workflows

| Workflow | Trigger | Purpose |
|---|---|---|
| `OpenHands PR Code Review` | PR opened / updated / ready for review | Review code and leave GitHub feedback |
| `OpenHands Issue Router` | Issue labeled | Route `oh:fix-bug` and `oh:add-ci-check` issues to the right skill |
| `OpenHands Dependency Management` | Manual + weekly schedule | Audit and update dependencies safely |
| `OpenHands Release Notes` | Manual | Draft release notes for a requested tag or ref range |
| `OpenHands Expand Test Coverage` | Manual | Add focused tests and open a draft PR |

## Labels to create in GitHub

Create these repository labels before the issue-driven demo:

| Label | Purpose |
|---|---|
| `oh:fix-bug` | Trigger OpenHands to fix a bug from an issue |
| `oh:add-ci-check` | Trigger OpenHands to create a new CI check from an issue |

## Required setup

### 1. Add the OpenHands API key

Add this GitHub Actions secret:

- `OPENHANDS_API_KEY` - your OpenHands Cloud API key

### 2. Connect the repo to OpenHands Cloud

For the full experience, the repository should be connected to OpenHands Cloud so the cloud agent can:

- read and write branches
- comment on issues and PRs
- open draft PRs

### 3. Create labels

Create `oh:fix-bug` and `oh:add-ci-check` in the GitHub repo.

## Demo issue templates and helper scripts

This repo includes issue templates and helper scripts so the flows are easy to demonstrate.

If your local `origin` still points to `swagger-api/swagger-petstore`, set `GITHUB_REPO=<your-demo-repo>` when using the helper scripts so they target your fork or demo repository.

### Create a sample bug-fix issue

```bash
python3 scripts/create_demo_issue.py --kind bug --scenario false-positive-notifier
```

### Create a sample CI-check issue

```bash
python3 scripts/create_demo_issue.py --kind ci --scenario api-smoke-check
```

### Dry-run an issue first

```bash
python3 scripts/create_demo_issue.py --kind bug --dry-run
```

### Smoke-test the cloud trigger path

```bash
python3 scripts/test_cloud_trigger.py --label oh:fix-bug
```

This creates a disposable issue, waits for the GitHub Action acknowledgement comment with an OpenHands conversation URL, then closes the issue.

## Repository layout for the demo

```text
.agents/
`-- skills/
    |-- code-review/
    |-- dependency-management/
    |-- release-notes/
    |-- fix-bug/
    |-- create-ci-check/
    `-- expand-test-coverage/

.github/
|-- ISSUE_TEMPLATE/
|-- openhands/
|   |-- cloud_client.py
|   `-- dispatch.py
`-- workflows/
    |-- openhands-pr-code-review.yml
    |-- openhands-issue-router.yml
    |-- openhands-dependency-management.yml
    |-- openhands-release-notes.yml
    `-- openhands-expand-test-coverage.yml

scripts/
|-- create_demo_issue.py
`-- test_cloud_trigger.py
```

## Suggested demo story

A clean end-to-end flow for a live demo:

1. Show the existing Petstore app and current workflows
2. Create a bug issue with `oh:fix-bug`
3. Show the acknowledgement comment and conversation link
4. Let OpenHands fix the bug and open a draft PR
5. Open a second issue with `oh:add-ci-check`
6. Show OpenHands adding a new smoke-test workflow
7. Run the manual `OpenHands Expand Test Coverage` workflow
8. Run the manual `OpenHands Release Notes` workflow
9. Open or update a PR to show `OpenHands PR Code Review`

## Good built-in demo targets from the current code

These are already visible in the Petstore controllers and make useful issue prompts:

- `PetController.findPetsByStatus(...)` sends a false error notifier even on successful responses
- `UserController.deleteUser(...)` returns a null entity on successful deletion
- `OrderController.deleteOrder(...)` returns a null entity on successful deletion

## Local validation

### Python dispatcher scripts

```bash
python3 -m py_compile \
  .github/openhands/cloud_client.py \
  .github/openhands/dispatch.py \
  scripts/create_demo_issue.py \
  scripts/test_cloud_trigger.py
```

### Maven verification

```bash
mvn --no-transfer-progress -B test
```

## Upstream Swagger Petstore app

This repo is based on the upstream Swagger Petstore sample hosted at <https://petstore3.swagger.io>.

### Run with Maven

```bash
mvn package jetty:run
```

This starts Jetty on port `8080`.

### Run with Docker

```bash
docker build -t swaggerapi/petstore3:unstable .
docker run --name swaggerapi-petstore3 -d -p 8080:8080 swaggerapi/petstore3:unstable
```

### Test the running server

Visit or curl:

- <http://localhost:8080/api/v3/openapi.json>
- <http://localhost:8080>

The UI is bundled in the sample and can be used for quick manual inspection.
