# OpenHands Petstore Demo

This repository turns the upstream Swagger Petstore sample into a GitHub-native OpenHands Cloud demo.

It is meant to show OpenHands acting as a useful coding agent for developers: taking in work from issues, pull requests, and manual workflows, making meaningful code or repository changes, and returning the results as reviewable pull requests.

It showcases these flows:

1. PR code review using an OpenHands skill
2. Dependency management from a manual or scheduled workflow
3. Release notes generation from a manual workflow
4. Tagged GitHub issue -> OpenHands bug-fix run (`oh:fix-bug`) -> draft PR
5. Tagged GitHub issue -> OpenHands CI-check run (`oh:add-ci-check`) -> draft PR
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

## Live validation status

This repository has been validated live against GitHub Actions and OpenHands Cloud in the public demo repo:

- PR code review flow
- `oh:fix-bug` issue routing -> OpenHands conversation -> draft bug-fix PR
- `oh:add-ci-check` issue routing -> OpenHands conversation -> draft CI-check PR
- manual dependency management workflow
- manual release notes workflow
- manual test expansion workflow

The live tests produced real OpenHands conversation links, GitHub comments, and draft PRs.

## Added OpenHands workflows

| Workflow | Trigger | Purpose |
|---|---|---|
| `OpenHands PR Code Review` | PR opened / updated / ready for review | Review code and leave GitHub feedback |
| `OpenHands Issue Router` | Issue labeled | Route `oh:fix-bug` and `oh:add-ci-check` issues into OpenHands conversations that aim to open draft PRs |
| `OpenHands Dependency Management` | Manual + weekly schedule | Audit and update dependencies safely |
| `OpenHands Release Notes` | Manual | Draft release notes for a requested tag or ref range |
| `OpenHands Expand Test Coverage` | Manual | Add focused tests and open a draft PR |

## Labels to create in GitHub

Create these repository labels before the issue-driven demo:

| Label | Purpose |
|---|---|
| `oh:fix-bug` | Trigger an OpenHands bug-fix run from an issue and aim for a draft PR |
| `oh:add-ci-check` | Trigger an OpenHands CI-check run from an issue and aim for a draft PR |

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

### 4. Enable Dependency Graph

If you want the inherited upstream `Dependency Review` pull-request check to pass, enable **Dependency Graph** in the repository security settings.

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

For presenter-facing guidance on which artifacts are currently safe to show live versus which ones are better treated as optional or cautionary examples, see [`DEMO_GUIDE.md`](./DEMO_GUIDE.md).


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

## Repo-local skills as a starting point

This demo uses custom repo-local skills under `.agents/skills/` so the behavior is easy to inspect, fork, and adapt.

If you want to extend this demo, these skills are a good starting point for your own GitHub-native automations. You can also browse the broader OpenHands skills catalog in [`OpenHands/extensions`](https://github.com/OpenHands/extensions/tree/main/skills) and reuse or adapt those skills where they fit.

In this repo, the custom skills are intentionally the main demo surface, while the upstream skills catalog is a useful reference library.

## Presenter guide

If you want a presenter-facing walkthrough, sample issue prompts, and a suggested story arc for a live demo, see [`DEMO_GUIDE.md`](./DEMO_GUIDE.md).

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
