# AGENTS.md - timefold-workshop

This repository inherits the workspace guidance from `../AGENTS.md`.
Read and follow the workspace root guide first. This file only adds
repo-specific layout, commands, domain rules, and local exceptions.


Timefold Solver workshop repository for constraint-solving examples, scheduling
problems, and Kotlin/Spring Boot integrations.

- Java 21
- Kotlin 2.3
- Spring Boot 3.x/3.4+
- Exposed for persistence examples

## Commands

Prefer module-scoped validation.

```bash
./gradlew build
./gradlew test
./gradlew :<module>:test
./gradlew :<module>:build
```

## Layout

`settings.gradle.kts` auto-registers modules from:

| Directory | Purpose |
|---|---|
| `00-shared/` | Shared test/support code |
| `01-quickstarts/` | Timefold quickstart examples |
| `exposed/` | Exposed-backed Timefold examples |

## Rules

- New dependencies go through `buildSrc/src/main/kotlin/Libs.kt`.
- Keep solver examples focused on constraint modeling clarity.
- When changing scheduling/domain models, add or update tests that prove the
  constraint behavior rather than only checking object construction.
- Kotlin uses Java 21 toolchain and Kotlin 2.3 language/api settings.
