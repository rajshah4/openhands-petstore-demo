# Repository Memory

## Repository identity
- Base app: cloned from `swagger-api/swagger-petstore`
- Primary language: Java (Maven)
- Default branch upstream: `master`

## OpenHands demo architecture
- GitHub Actions trigger a lightweight Python dispatcher in `.github/openhands/dispatch.py`.
- The dispatcher starts OpenHands Cloud conversations using the V1 `app-conversations` API.
- Repo-local skills live under `.agents/skills/`.
- Issue labels route coding tasks:
  - `oh:fix-bug`
  - `oh:add-ci-check`
- Manual workflows exist for dependency management, release notes, and test expansion.
- PRs trigger the `code-review` skill via `pull_request_target`.

## Setup prerequisites
- Repo secret required: `OPENHANDS_API_KEY`
- The repository should be connected to OpenHands Cloud / GitHub integration so cloud conversations can comment, branch, and open PRs.
- Recommended helper scripts:
  - `python3 scripts/create_demo_issue.py --dry-run`
  - `python3 scripts/test_cloud_trigger.py --label oh:fix-bug`

## Validation commands
- Python syntax checks:
  - `python3 -m py_compile .github/openhands/cloud_client.py .github/openhands/dispatch.py scripts/create_demo_issue.py scripts/test_cloud_trigger.py`
- Maven verification:
  - `mvn --no-transfer-progress -B test`
  - `mvn --no-transfer-progress -B install --file pom.xml`

## Useful code hotspots
- `src/main/java/io/swagger/petstore/controller/PetController.java`
- `src/main/java/io/swagger/petstore/controller/UserController.java`
- `src/main/java/io/swagger/petstore/controller/OrderController.java`

## Demo-friendly bug ideas already visible in code
- `PetController.findPetsByStatus(...)` currently triggers a `Pets not found` notifier even on successful responses.
- `UserController.deleteUser(...)` returns a null entity after successful deletion.
- `OrderController.deleteOrder(...)` returns a null entity after successful deletion.

## Existing upstream CI to avoid duplicating
- `.github/workflows/maven.yml`
- `.github/workflows/maven-pulls.yml`
- `.github/workflows/dependency-review.yml`
- `.github/workflows/codeql-analysis.yml`

## Safety / scope notes
- Do not publish releases or deploy Docker images as part of the OpenHands demo workflows unless explicitly requested.
- Keep new CI checks focused and fast; avoid overlapping with the existing Maven build jobs.

## Live-testing learnings (2026-04-06)
- The OpenHands `app-conversations` API may return a runtime `conversation_url`; for GitHub comments, prefer the stable app URL format: `https://app.all-hands.dev/conversations/<conversation_id>`.
- Both issue-label triggers were validated live and successfully posted acknowledgement comments plus launched real OpenHands work.
- Manual workflows for dependency management, release notes, and test expansion were all validated live and each opened draft PRs.
- Re-running the bug-fix smoke test created duplicate draft bug-fix PRs because each labeled issue continued running after the disposable issue was closed.
- After switching the repo to public, the inherited `Code scanning - action` check recovered successfully on rerun.
- After enabling Dependency Graph in repository settings, `Dependency Review` also recovered and all PR checks passed on the smoke-test PR.
- Final public-demo state: the repo is public, `OPENHANDS_API_KEY` is configured, Dependency Graph is enabled, and the smoke-test PR achieved all-green checks.
- Cleanup pass closed duplicate PR #4 and the docs-only smoke-test PR #6, leaving representative demo PRs #5, #7, #8, #9, and #10 open.
- Documentation split: `README.md` is for replication/setup/testing, while `DEMO_GUIDE.md` is for presenters running the live story.
