# Release Notes — skill-validation-v0.1.1

**Release tag:** `skill-validation-v0.1.1`  
**Date:** 2026-04-08  
**Comparison range:** `demo-v0.1.0` (`c287a07`) → `master` (`f3f345c`)  
**Branch:** `master`

---

## Overview

`skill-validation-v0.1.1` is a **documentation and skills refinement release** that follows the initial
`demo-v0.1.0` launch. This release adopts official OpenHands skills from the
[OpenHands/extensions](https://github.com/OpenHands/extensions) repository, adds comprehensive presenter
documentation, and records live validation learnings from real-world GitHub/OpenHands Cloud testing.

**Key Highlights:**
- Official `code-review` and `releasenotes` skills adopted from OpenHands/extensions
- New presenter guide (`DEMO_GUIDE.md`) with validated demo story paths and safety notes
- Enhanced repository memory with live testing insights and artifact quality assessments
- Refined documentation clarifying the demo architecture and mixed skill model

---

## 📚 Documentation Improvements

### New: Presenter Demo Guide

**Added:** `DEMO_GUIDE.md` — A comprehensive 170-line guide for presenters and demoers, split from
`README.md` to separate presentation concerns from replication/setup instructions.

**Contents:**
- Live-test validation status for all demo flows
- Pre-demo checklist (repository settings, secrets, labels)
- Safe artifact recommendations (which PRs to show vs. cautionary examples)
- Two demo story paths: safer artifact-based vs. optional fresh live runs
- Sample issue drafts for triggering `oh:fix-bug` and `oh:add-ci-check` flows
- Step-by-step walkthrough for each demo scenario

**Why this matters:** Presenters can now confidently demonstrate OpenHands capabilities without
improvising prompts or risking live failures. The guide distinguishes validated artifacts
(e.g., PR #9, #10) from cautionary examples (e.g., PR #5 with failing smoke test).

> Commit: `83615c2` — _Split presenter guide from replication README_  
> Follow-ups: `659fff4` (sample issue drafts), `ee2fde0` (artifact validation)

### Enhanced: README Restructuring

**Changed:** `README.md` restructured to focus on replication, setup, and technical architecture.

**Key improvements:**
- Clarified that `oh:fix-bug` and `oh:add-ci-check` **start OpenHands runs** that create conversations
  and aim to open draft PRs (not just simple routing labels)
- Documented the mixed skill model: official skills from `OpenHands/extensions` (code-review, releasenotes)
  are vendored locally, while other flows remain custom repo-local skills
- Updated wording to reflect the demo as a GitHub-native OpenHands showcase rather than generic
  automation

> Commits: `a21900b`, `1d6d347`, `eef553f`

### Updated: Repository Memory

**Changed:** `AGENTS.md` expanded with 22 new lines of live testing learnings.

**New insights recorded:**
- OpenHands `app-conversations` API conversation URL handling best practices
- Smoke test observations (duplicate PRs from re-runs, CI check recovery steps)
- Public repo migration checklist (Dependency Graph, inherited checks)
- Artifact quality assessments for PRs #5–#12
- Documentation conventions (README vs. DEMO_GUIDE.md separation)

**Why this matters:** OpenHands agents in future conversations benefit from accumulated knowledge,
avoiding repeat mistakes and making informed decisions about PR quality and demo safety.

> Commits: `ed2c0b6`, `938a6c4`, `512c0dd`, `ee2fde0`

---

## 🤖 Skills Adoption & Refinement

### Adopted: Official OpenHands Skills

**Changed:** Replaced custom skills with official versions from [OpenHands/extensions](https://github.com/OpenHands/extensions).

| Skill | Change | Impact |
|---|---|---|
| **`code-review`** | Updated from repo-local version to official `OpenHands/extensions` skill (83 line diff) | Standardized PR review process with upstream improvements |
| **`releasenotes`** | Renamed from `release-notes` and synced with official skill | Consistent changelog generation aligned with OpenHands best practices |

**Why this matters:** Using official skills ensures compatibility with future OpenHands updates,
reduces maintenance burden, and provides a reference implementation for teams extending the demo.

**Migration notes:**
- `.agents/skills/release-notes/` → `.agents/skills/releasenotes/` (renamed directory)
- `.agents/skills/code-review/SKILL.md` significantly enhanced with progressive disclosure structure
- Workflow reference updated: `.github/workflows/openhands-release-notes.yml` now uses `releasenotes`

> Commit: `f3f345c` — _Adopt official OpenHands review and release skills_

### Updated: Issue Templates

**Changed:** Minor wording updates to issue templates for consistency.

**Files affected:**
- `.github/ISSUE_TEMPLATE/fix-bug-demo.md`
- `.github/ISSUE_TEMPLATE/add-ci-check-demo.md`

These templates pre-populate the `oh:fix-bug` and `oh:add-ci-check` labels for self-service demo
triggering.

---

## 🔍 Live Validation & Quality Assessment

This release records comprehensive live testing results across all demo workflows:

### Successfully Validated Flows ✅

- **PR code review** — `code-review` skill triggered on `pull_request_target`
- **Issue-triggered bug fix** — `oh:fix-bug` label → OpenHands run → draft PR
- **Issue-triggered CI check** — `oh:add-ci-check` label → OpenHands run → draft PR
- **Manual dependency management** — Workflow dispatch → dependency audit → draft PR
- **Manual release notes** — Workflow dispatch → changelog generation (this file)
- **Manual test expansion** — Workflow dispatch → JUnit test additions

### Artifact Quality (as of 2026-04-08)

| PR # | Title | Status | Recommendation |
|---|---|---|---|
| #9 | Fix controller bugs: false-positive notifier, null entities | Open, build passes ✅ | **Best bug-fix artifact** to show |
| #10 | Add controller regression tests (37 tests) | Open, all checks green ✅ | **Strongest hero PR** with full CI pass |
| #7 | Release notes for demo-v0.1.0 | Open, checks passed on rerun ✅ | Good release-notes artifact |
| #8 | Safe dependency updates (April 2026) | Open, checks passed on rerun ✅ | Good dependency-mgmt artifact |
| #6 | Smoke test PR for code review | Closed | Historical example for review flow |
| #5 | API smoke-test CI workflow | Open, smoke test fails ⚠️ | Cautionary; frame as guardrails example |
| #12 | Fix findPetsByStatus false-positive | Open, build fails ⚠️ | Cautionary; do not use as primary demo |
| #13 | Validate official code-review skill wiring | Open, just created | Temporary validation PR for new skills |

---

## 🛠️ Technical Changes

### Files Changed

```
.agents/skills/code-review/SKILL.md           |  83 ++++++++++++++++++---
.agents/skills/release-notes/SKILL.md         |  30 ------  (removed)
.agents/skills/releasenotes/SKILL.md          |  20 +++++ (added)
.github/ISSUE_TEMPLATE/add-ci-check-demo.md   |   2 +-
.github/ISSUE_TEMPLATE/fix-bug-demo.md        |   2 +-
.github/workflows/openhands-release-notes.yml |   2 +-
AGENTS.md                                     |  22 +++++-
DEMO_GUIDE.md                                 | 170 ++++++++++ (new file)
README.md                                     |  66 +++++++++------
```

**Total:** 9 files changed, 315 insertions(+), 82 deletions(-)

### Commit Summary

| Commit | Message | Date |
|---|---|---|
| `f3f345c` | Adopt official OpenHands review and release skills | 2026-04-08 |
| `ee2fde0` | Validate demo guide against current live artifacts | 2026-04-08 |
| `659fff4` | Add sample issue drafts to demo guide | 2026-04-08 |
| `eef553f` | Document repo-local skills as extension starting points | 2026-04-06 |
| `1d6d347` | Clarify issue-triggered OpenHands runs in docs | 2026-04-06 |
| `a21900b` | Clarify coding agent positioning in README | 2026-04-06 |
| `83615c2` | Split presenter guide from replication README | 2026-04-06 |
| `512c0dd` | Record final dependency review fix | 2026-04-06 |
| `938a6c4` | Update repo memory after public visibility retest | 2026-04-06 |
| `ed2c0b6` | Record live demo testing learnings | 2026-04-06 |

---

## 📦 Comparison Summary

| Category | Details |
|---|---|
| Base commit | `c287a07` (demo-v0.1.0) |
| Head commit | `f3f345c` (current master) |
| Demo-specific commits | 10 commits over 2 days |
| New files | 1 (`DEMO_GUIDE.md`) |
| Renamed files | 1 (`.agents/skills/release-notes/` → `.agents/skills/releasenotes/`) |
| Removed files | 1 (old `release-notes/SKILL.md`) |
| Lines added | 315 |
| Lines removed | 82 |

---

## ✅ Verification

The Maven build continues to pass cleanly at this revision:

```bash
mvn --no-transfer-progress -B test
```

**No breaking changes** introduced. All changes are documentation-focused with skill metadata updates.
The demo infrastructure established in `demo-v0.1.0` remains fully functional.

**Live validation status:** All six demo workflows (PR review, bug fix, CI check, dependency mgmt,
release notes, test expansion) have been manually tested end-to-end in GitHub and OpenHands Cloud,
producing real conversations and draft PRs.

---

## 🎯 Migration Notes

If you forked this demo at `demo-v0.1.0` and want to adopt these changes:

1. **Rename skill directory:**
   ```bash
   git mv .agents/skills/release-notes .agents/skills/releasenotes
   ```

2. **Update workflow reference:**
   Edit `.github/workflows/openhands-release-notes.yml` to reference `releasenotes` skill.

3. **Adopt official skills (optional but recommended):**
   ```bash
   # Vendor official code-review and releasenotes skills from OpenHands/extensions
   # See commit f3f345c for exact file changes
   ```

4. **Add presenter guide:**
   Copy `DEMO_GUIDE.md` and review the sample issue drafts for your demo scenarios.

5. **Update repository memory:**
   Merge live testing insights from `AGENTS.md` commits `ed2c0b6`, `938a6c4`, `512c0dd`, `ee2fde0`.

---

## 🔮 What's Next

Suggested areas for future releases:

- **Test Infrastructure:** Address failing smoke test in PR #5 to demonstrate clean CI-check generation
- **Additional Skills:** Consider vendoring more OpenHands/extensions skills (e.g., `learn-from-code-review`)
- **Artifact Curation:** Merge validated hero PRs (#9, #10) and close cautionary examples
- **Presenter Materials:** Add slide deck templates or video walkthrough references to `DEMO_GUIDE.md`
- **Automation:** Consider scheduled dependency audits or automatic release notes on tag creation

---

## 📄 Open Draft PRs (pending review)

The following draft PRs remain open and are available as demo artifacts:

- **[#13](https://github.com/rajshah4/openhands-petstore-demo/pull/13)** —
  `Validate official code-review skill wiring`  
  Temporary PR to validate GitHub-native PR review flow after skill adoption.

- **[#12](https://github.com/rajshah4/openhands-petstore-demo/pull/12)** —
  `Fix false-positive error notification in PetController.findPetsByStatus`  
  Fresh bug-fix run; currently fails build (cautionary example).

- **[#10](https://github.com/rajshah4/openhands-petstore-demo/pull/10)** —
  `Add controller regression tests for Pet, User, and Order APIs (37 tests)` ✅  
  **Recommended hero artifact:** All checks green, excellent test coverage example.

- **[#9](https://github.com/rajshah4/openhands-petstore-demo/pull/9)** —
  `Fix controller bugs: false-positive notifier, null delete entities` ✅  
  **Recommended hero artifact:** Clean build pass, demonstrates bug-fix workflow.

- **[#8](https://github.com/rajshah4/openhands-petstore-demo/pull/8)** —
  `chore: safe minor/patch dependency updates (April 2026)` ✅  
  Dependency management workflow artifact.

- **[#7](https://github.com/rajshah4/openhands-petstore-demo/pull/7)** —
  `docs: release notes for demo-v0.1.0` ✅  
  Release notes workflow artifact (previous release).

- **[#5](https://github.com/rajshah4/openhands-petstore-demo/pull/5)** —
  `ci: add API smoke-test workflow (Jetty + openapi.json probe)` ⚠️  
  CI-check workflow artifact; generated smoke test currently fails (use as guardrails example).

---

## 👥 Contributors

All commits in this release: **Rajiv Shah** (@rajshah4)

---

_These release notes were generated by OpenHands on behalf of the repository operator_  
_(workflow run [#24146957761](https://github.com/rajshah4/openhands-petstore-demo/actions/runs/24146957761), triggered by @rajshah4)._
