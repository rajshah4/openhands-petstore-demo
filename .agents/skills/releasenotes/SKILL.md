---
name: releasenotes
description: Generate formatted changelogs from git history since the last release tag. Use when preparing release notes that categorize changes into breaking changes, features, fixes, and other sections.
triggers:
- /releasenotes
---

Generate a changelog for all changes from the most recent release until now.

## Steps
1. Find the most recent release tag using `git tag --sort=-creatordate`
2. Get commits and merged PRs since that tag
3. Look at previous releases in this repo to match their format and style
4. Categorize changes into sections: Breaking Changes, Added, Changed, Fixed, Notes
5. Focus on user-facing changes (features, important bug fixes, breaking changes)
6. Include PR links and contributor attribution
7. For this GitHub-native demo, prefer committing the changelog as a reviewable markdown artifact or draft PR when that is more useful than returning text only

## Output
Present the changelog in a markdown code block, ready to copy-paste into a GitHub release. When running in this repo's workflow context, prefer a reviewable markdown artifact or draft PR when practical.
