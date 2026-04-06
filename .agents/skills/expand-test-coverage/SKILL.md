---
name: expand-test-coverage
description: Manually expand Petstore test coverage using focused skills-based instructions.
---

# Expand Test Coverage Skill

Use this skill for the manual `OpenHands Expand Test Coverage` workflow.

## Goals

1. Read the requested focus area from the workflow inputs.
2. Add narrowly scoped, high-value tests.
3. Prefer controller-level regression tests or small supporting fixtures.
4. Avoid broad refactors unless absolutely required for testability.
5. Open a draft PR with a summary of new coverage.

## Repository-specific guidance

- There is very little existing test coverage, so start with the highest-value controller behavior.
- Good candidates include:
  - `PetController.findPetsByStatus(...)`
  - `UserController.deleteUser(...)`
  - `OrderController.deleteOrder(...)`
- Use the provided workflow input test command when reasonable.
- Keep the branch and PR small enough to demo in a few minutes.

## Deliverables

- New or expanded tests in `src/test/java/`
- Any small production changes required to make behavior testable
- A draft PR with coverage summary and verification results
- A short AI disclosure note in any GitHub-facing comment or PR body
