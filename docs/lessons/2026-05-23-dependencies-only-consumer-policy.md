# Dependencies-Only Consumer Policy

## Context

The Timefold workshop pinned bluetape4k artifact versions directly. During a
release upgrade, that exposed an obsolete `bluetape4k-spring-boot3-core`
coordinate that is not present in the current release line.

## Decision

Use `bluetape4k-dependencies` as the only bluetape4k version source and declare
bluetape4k artifacts without versions. Keep historical accessor names only when
they point to valid BOM-managed coordinates.

## Outcome

The catalog now imports `bluetape4k-dependencies`, removes direct bluetape4k
version refs, and points the Spring Boot core alias at the current
`bluetape4k-spring-boot-core` artifact.

## Verification

Ran forbidden-reference grep, `git diff --check`, and
`./gradlew compileKotlin --no-daemon --no-configuration-cache`.

## Future Guidance

If a bluetape4k artifact cannot resolve after a BOM upgrade, check whether the
artifact coordinate changed before adding a local version override.
