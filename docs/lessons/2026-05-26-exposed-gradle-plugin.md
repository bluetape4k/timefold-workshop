## Context

Adopted the JetBrains Exposed Gradle plugin for Timefold Exposed example modules.

## Decision

The workshop stays independent from the managed `bt4k` catalog. Its local JetBrains Exposed BOM line was moved to 1.3.0 because the official Exposed plugin is available for that line.

## Outcome

JDBC and R2DBC Exposed example modules now apply the plugin and expose `generateMigrations`.

## Verification

Ran `git diff --check`, `./gradlew -q help`, and `:exposed-jdbc-examples:tasks --all`.

## Future Guard

Do not tie workshop plugin versions to `bluetape4k-dependencies` catalog refs; keep local aliases explicit unless the repo becomes a managed library repo.
