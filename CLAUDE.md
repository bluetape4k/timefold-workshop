# CLAUDE.md - timefold-workshop

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

| Directory | Purpose |
|---|---|
| `00-shared/` | Shared test/support code |
| `01-quickstarts/` | Timefold quickstart examples |
| `exposed/` | Exposed-backed Timefold examples |

## Rules

- Keep solver examples focused on constraint modeling clarity.
- When changing scheduling/domain models, add or update tests that prove the
  constraint behavior rather than only checking object construction.
- Store shared README images under `docs/assets/`.
