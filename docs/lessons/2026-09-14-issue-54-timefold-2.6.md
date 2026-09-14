# Issue #54 Timefold Solver 2.6.0 검증 lesson

작성일: 2026-09-14
대상: `bluetape4k/timefold-workshop`
검증 브랜치: `feat/issue-54-timefold-2.6-verification`

## Context

중앙 `bluetape4k-dependencies` BOM이 관리하는 Timefold Solver `2.6.0`을
기존 workshop에 적용하면서, 2.2→2.6 중간 migration 근거와 실제 동작을
하나의 재현 가능한 증적으로 묶어야 했다. 요구사항은 dependency graph,
school/bed join·filter 증분 점수, SolverManager lifecycle, planning-list
element shadow, Exposed JDBC/R2DBC, 한영 실행 문서였다.

## Decision

1. Timefold 버전 소유권은 중앙 BOM에 남기고 workshop의
   `libs.timefold.solver.*` alias는 versionless로 유지했다. `buildSrc`에
   남아 있던 참조 없는 `1.32.0` helper만 사용처 검색과 compile 뒤 제거했다.
2. 기존 domain을 대규모 변환하지 않고 school/bed에 작은
   `EnvironmentMode.FULL_ASSERT` fixture를 추가했다. static
   `ConstraintVerifier`는 match 보조 증거로만 사용했다.
3. school 완료 상태는 entry별 `ReentrantLock`과
   `PENDING → COMPLETED|FAILED` 단일 전이를 갖는 `TimetableJobRegistry`로
   모았다. best/final/failure callback이 뒤섞여도 terminal 결과를 덮어쓰지
   않도록 했다.
4. list-variable 학습 모델은 shared 모듈에 독립적으로 두고
   `@PlanningListVariable` → `@IndexShadowVariable` → 선언형
   `@ShadowVariable` chain을 `SolutionManager.updateShadowVariables`와
   FULL_ASSERT solver로 검증했다.
5. 2.6 Neighborhoods/custom move와 Enterprise `ScoreAnalysis`는 현재
   active source/Community artifact에 사용 surface가 없으므로 도입하지 않고
   N/A/disabled로 명시했다. benchmark도 runtime graph 부재를 N/A로 남겼다.

## Migration surprises and failures

- 첫 bed incremental fixture는 기존 `Department → Room → Bed → Room`
  back-reference가 `toString` 중 재귀되어 `StackOverflowError`를 냈다. solver
  value-range fixture의 back-reference를 비우고 static gender fixture를
  분리해 테스트만 고쳤으며, 기존 domain의 unrelated `toString` API는
  확장하지 않았다.
- 첫 R2DBC 전체 실행은 테스트가 시작되기 전
  `:exposed-r2dbc-examples:compileTestKotlin`에서 Gradle daemon이 사라졌다.
  daemon log에는 사용자 중단에 따른 정상 종료만 있었고 hs_err/Kotlin
  exception은 없었다. `--no-daemon --max-workers=1`로 compile과 전체
  suite를 재실행해 24 tests를 PASS했다. repository 설정과 R2DBC H2
  `1.1.0.RELEASE`/H2 `2.4.240` ABI pin은 변경하지 않았다.
- 중앙 publication POM verifier는 `bluetape4k-projects`의 기존 dirty
  변경 때문에 전체 publisher를 시작하지 않았다. 해당 변경을 건드리지
  않고 보존한 채 clean publisher 8곳만 실행해 POM 110개와 Maven model
  110개를 모두 검증했다. 전체 9-publisher PASS로 과장하지 않는다.
- 계획 초안의 `:exposed:jdbc-examples` 표기는 settings가 실제로 노출하는
  `:exposed-jdbc-examples`와 달랐다. `./gradlew projects`를 기준으로 계획과
  README의 명령을 모두 정정했다.

## Outcome

- 구현·테스트·문서는 중앙 BOM, 기존 module topology, Kotlin public API 재사용
  경계를 지킨다.
- root와 기존 module README/README.adoc는 현재 Spring Boot/WebFlux endpoint,
  list-shadow annotation, lifecycle/증분 명령을 같은 의미로 설명한다.
- registry map의 retention/TTL과 production metrics는 기존 HTTP 계약·교육용
  범위에 없어 후속 운영/API 결정으로 남겼다.

## Exact verification

| 대상 | 결과 |
|---|---|
| `:bluetape4k-timefold:test` | 5 tests, failures/errors/skipped 0 |
| `:school-timetabling:test` | 41 tests, failures/errors 0, Enterprise disabled 1 |
| `:bed-allocation:test` | 41 tests, failures/errors/skipped 0 |
| `:exposed-jdbc-examples:test` | 48 tests, failures/errors/skipped 0 |
| `:exposed-r2dbc-examples:test` | 24 tests, failures/errors/skipped 0 |
| root `test` | 159 tests, failures/errors 0, disabled 1, BUILD SUCCESSFUL |
| root `build` | exit 0, BUILD SUCCESSFUL |
| clean publisher POM verifier | failures 0, repositories 8, POM 110, dependencies 17,566, Maven models 110 |
| docs `git diff --check` 및 path/keyword scan | PASS |

모든 Gradle 명령은 feature worktree에서 no-daemon 단일 worker와 no-build-cache
옵션으로 순차 실행했다. 테스트 XML의 `skipped`/disabled는 PASS 수에 포함하지
않았고, Enterprise 항목은 N/A로 분리했다.

## Review misses and future guards

- 구현 전 review가 AsciiDoc renderer 실행까지 요구하지 않아, 이번 검증은
  `git diff --check`와 경로/키워드 정합성에 한정됐다. AsciiDoc 도구가 표준화되면
  CI에서 두 README 포맷을 렌더링하는 별도 guard를 추가한다.
- `TimetableJobRegistry`의 unbounded map은 현재 계약에 retention 의미가 없어
  남겼다. 운영 API가 장기 실행되면 TTL/상한/관찰성 설계를 먼저 승인하고
  callback race 테스트를 그대로 보존한다.
- 중앙 publication verifier는 dirty sibling을 자동으로 덮어쓰지 않았다.
  publisher별 clean preflight와 dirty repository 목록을 receipt에 고정해,
  다른 작업자의 변경을 보존하면서 재실행할 수 있게 한다.
- 2.6 Neighborhoods와 benchmark를 사용하지 않는 것은 기능 누락이 아니라
  source/graph 근거에 따른 N/A다. 새 adoption을 시작할 때는 성능 목표와
  Type F 평가를 별도 계획으로 만든다.
