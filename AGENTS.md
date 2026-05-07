# AGENTS.md - timefold-workshop

Kotlin/Spring workshop repository for Timefold Solver examples. It demonstrates
constraint-solving problems such as timetables, appointments, event placement,
conflict avoidance, duration, and priority optimization.

## Stack

- Java 21 toolchain.
- Kotlin 2.3 compiler settings in Gradle.
- Spring Boot 3.x.
- Timefold Solver.
- Kotlin Exposed for persistence examples.
- JUnit 5 for tests.

## Layout

`settings.gradle.kts` registers modules through `includeModules()`.

| Path | Purpose |
|---|---|
| `00-shared/bluetape4k-timefold` | Shared Timefold utilities and common code |
| `01-quickstarts/bed-allocation` | Bed allocation quickstart |
| `01-quickstarts/school-timetabling` | School timetabling quickstart |
| `exposed/jdbc-examples` | Exposed JDBC examples |
| `exposed/r2dbc-examples` | Exposed R2DBC examples |
| `buildSrc/src/main` | Version, plugin, and dependency constants |

## Commands

```bash
./gradlew build
./gradlew test
./gradlew :bluetape4k-timefold:test
./gradlew :bed-allocation:test
./gradlew :school-timetabling:test
./gradlew :jdbc-examples:test
./gradlew :r2dbc-examples:test
```

Check `settings.gradle.kts` before assuming a module path. Prefer narrow module
tests for narrow changes.

## Rules

- Solver domain changes must review planning entities, planning variables,
  constraint providers, and score calculation paths together.
- Constraint changes need tests that verify expected score or assignment output.
- Do not mix existing JDBC and R2DBC Exposed transaction patterns casually.
- New dependencies or version changes should follow the existing `buildSrc`
  constants pattern.
- Apply bluetape4k Kotlin rules: `require*` validation, `KLogging`, coroutine
  cancellation propagation, and Testcontainers singleton launchers.

## Verification

- For docs-only changes, verify README links and module names against the
  current layout.
- For Kotlin or Gradle changes, run affected module tests.
- For shared build settings, run `./gradlew test` or all affected module tests.
