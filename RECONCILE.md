# Reconcile before merging to main

Scope: `integration/spring-boot-4.1`. This file is deleted by the merge commit — if it
still exists on `main`, the merge skipped these checks.

Run them after **every rebase onto main**, not just before the merge. Main keeps growing the
things this branch had to change, so each rebase can re-import them.

## Reason phrases and status constants

RFC 9110 renamed the phrases and Spring 7 renamed the constants. Main still carries the old
spelling, so a rebase re-imports it.

```bash
git grep -F -c -e "Unprocessable Entity" -e "Unprocessible Entity" \
               -e "Payload Too Large" -- src ; # expect: no output
git grep -F -c -e "UNPROCESSABLE_ENTITY" -e "PAYLOAD_TOO_LARGE" -- src ; # expect: no output
```

## Version coordinate

This branch is `3.0.0-SNAPSHOT`, a local coordinate kept apart from `main`'s snapshot in the local Maven repository. `core`'s integration branch consumes it from there. At the merge, the version returns to `main`'s, which follows the platform release.
