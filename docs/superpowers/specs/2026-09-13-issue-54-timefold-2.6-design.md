# Issue #54 설계 명세: Timefold Solver 2.6.0 검증과 학습 예제 확장

상태: 승인된 Type A 실행 계획의 설계 명세
작성일: 2026-09-13
대상: `bluetape4k/timefold-workshop` Issue #54
기준 브랜치: `origin/develop` (`db724ee0f4c848770169651d5851188ae608db3c`)
구현 브랜치: `feat/issue-54-timefold-2.6-verification`

## 1. 목표와 성공 조건

이 명세는 Timefold Solver `2.6.0` 전환 후보를 예제 저장소에서 재현 가능한
증거로 검증하고, Kotlin/Spring 사용자가 다음 변경을 실제 코드로 학습할 수
있게 하는 경계를 고정한다.

성공 조건은 다음과 같다.

1. 현재 `core`, `jackson`, `spring-boot-starter`의 선언·해결 버전과
   `2.2 → 2.3 → 2.4 → 2.5 → 2.6`의 공식 마이그레이션 차이를 한 문서와
   검증 로그로 연결한다. 중간 버전을 `2.5`와 `2.6`의 차이만으로 추론하지
   않는다.
2. 중앙 `bluetape4k-dependencies` BOM 소유권을 유지하면서 workshop의
   선언 버전과 실제 해석 버전을 일치시킨다.
3. school/bed 제약의 join 진입·이탈과 filter 참/거짓 전환에서 증분 점수가
   stale, 누락 또는 중복되지 않음을 작은 결정론적 fixture로 증명한다.
4. `SolverManager`의 submit → best/final completion, early termination,
   exception 경로에서 완료 결과의 유실·중복이 없음을 증명한다.
5. 기존 모듈에 독립적인 `@PlanningListVariable` element 예제를 추가하고,
   선언적 `@ShadowVariable`/`@ShadowSources`가 원본 변경과 재배치에 따라
   갱신되는 것을 `SolutionManager.updateShadowVariables`로 검증한다.
6. 현재 저장소에 Neighborhoods 사용이 없으면 구체적인 N/A 증거를 남기고,
   불필요한 custom move나 preview API 도입을 하지 않는다.
7. Exposed JDBC/R2DBC 영속성 테스트와 한영 README가 동일한 2.6 계약을
   설명한다.

## 2. 현재 상태와 근거 스냅샷

| 항목 | 기준 근거 | 설계상 의미 |
|---|---|---|
| Workshop HEAD | `db724ee0f4c848770169651d5851188ae608db3c` | 새 worktree의 immutable 기준점 |
| Worktree 절차 커밋 | `eaa0e40fcdff5029a672e450590e804c35665d6a` | `.worktrees/`가 저장소 변경으로 섞이지 않음 |
| 중앙 catalog | `io.github.bluetape4k:bluetape4k-dependencies:2.1.0-SNAPSHOT` | catalog source를 Maven BOM과 혼동하지 않음 |
| Timefold catalog | versionless alias, 중앙 BOM 관리 | local Timefold 버전을 새로 hard-code하지 않음 |
| 해결 그래프 | `core`, `jackson`, `spring-boot-starter` 모두 `2.6.0` | 선언·해결 그래프를 테스트 receipt로 고정 |
| 통합 메타데이터 | Timefold starter가 Spring Boot `4.1.1`/Jackson 3 계열을 메타데이터에 선언하지만 현재 그래프는 Boot `3.5.13`과 공존 | Boot/Jackson 경계에 대한 명시적 호환성 검증 필요 |
| benchmark | `school-timetabling:runtimeClasspath`에 일치 항목 없음 | benchmark 소비를 추론하지 말고 N/A로 기록 |
| stale buildSrc | `Versions.timefold_solver = "1.32.0"`와 미사용 `timefold_solver_*` alias | 사용처 확인 후 제거하거나 catalog/BOM 소유권과 충돌하지 않게 정리 |
| planning list/shadow | 현재 active source에 `@PlanningListVariable`, `@ShadowVariable`, `@ShadowSources` 없음 | shared 모듈에 독립 학습 fixture를 둠 |
| Neighborhoods | 현재 active source에 `Neighborhoods` 사용 없음 | preview API/custom move는 도입하지 않음 |
| 기존 회귀 테스트 | school/bed `ConstraintVerifier`, environment FULL_ASSERT, controller 및 Exposed round-trip 테스트 존재 | 기존 테스트를 보존하고 증분·수명주기 축을 추가 |

해결 그래프는 다음 명령을 fresh 실행한 결과를 기준으로 한다.

```bash
./gradlew :school-timetabling:dependencies --configuration runtimeClasspath
./gradlew :school-timetabling:dependencyInsight \
  --dependency timefold-solver-benchmark \
  --configuration runtimeClasspath
```

두 명령은 `BUILD SUCCESSFUL`이며 benchmark insight는 `No dependencies
matching given input were found`를 반환했다. 기준 `./gradlew test`도
`BUILD SUCCESSFUL`, `29 actionable tasks`, `school-timetabling:test 30
passing`이었다. Gradle deprecated-feature와 Netty Unsafe 경고는 기준
경고로 보존하며 새 변경의 PASS로 오인하지 않는다.

## 3. 범위와 제외 경계

### 포함

- `gradle/libs.versions.toml`, 사용하지 않는 `buildSrc` Timefold 선언의
  정리 여부, 실제 dependency graph와 publication 영향 검증
- school/bed의 join/filter 증분 회귀 fixture와 solver 환경 검증
- school `SolverManager` 완료·종료·실패 계약을 검증하는 테스트와 필요한
  최소 구현 보완
- `00-shared/bluetape4k-timefold`에 list-variable element와 declarative
  shadow 학습 모델·테스트 추가
- Exposed JDBC/R2DBC 테스트, root/shared/quickstart README 한영 정합성,
  migration/graph 증거 문서

### 제외

- 중앙 `bluetape4k-dependencies` BOM 재편집 또는 새 Maven publication
- Timefold Service, Enterprise 기능/PAT, Enterprise 전용 `ScoreAnalysis`
- 근거 없는 새 Gradle module 등록
- Neighborhoods preview API 또는 custom move 도입
- 내부 `ScoreDirector` 구현 패키지 의존
- 기존 모델 버그가 재현되지 않았는데 수행하는 무관한 constraint 수정
- 정적 `ConstraintVerifier` 결과만으로 증분 계약을 PASS 처리하는 것

## 4. 선택한 설계와 대안

### 대안 A — 기존 모듈에 독립 fixture를 추가한다 (선택)

shared 모듈에 최소 planning-list 모델을 두고, quickstart에는 실제 운영 경로의
증분·수명주기 회귀만 추가한다. 중앙 BOM과 기존 module topology를 보존하고
실행 시간이 짧은 fixture를 유지할 수 있다.

### 대안 B — 새 `list-shadow-example` module을 등록한다

학습 경계는 명확하지만 settings, catalog, README, CI, publication 검증을
모두 확장해야 한다. Issue #54의 증거 목적에 비해 module 등록 blast radius가
커서 선택하지 않는다.

### 대안 C — 현재 quickstart domain을 list variable로 대규모 변환한다

실제 도메인과 가까우나 기존 school/bed의 교육 목적과 API를 동시에 바꾸며,
2.6 migration 증거와 회귀 원인을 분리하기 어렵다. 이번 작업에서는 독립
fixture로 새 계약을 고정하고 기존 모델은 보존한다.

## 5. 구성 요소별 설계

### 5.1 의존성 그래프와 migration ledger

1. `libs.versions.toml`의 versionless Timefold alias와 root의 중앙 BOM import를
   소유권 기준으로 삼는다.
2. 현재 `2.6.0` 해결 결과를 `2.2` 기준 커밋(`e47496a`의 local catalog)과
   비교하고, 공식 release/upgrade 문서를 `2.3`, `2.4`, `2.5`, `2.6` 각
   행으로 기록한다. 행마다 “변경”, “이 저장소 영향”, “검증 명령”, “해당
   없음/보류 이유”를 채운다.
3. `core`, `jackson`, `spring-boot-starter`, benchmark를 선언·runtime·test
   configuration별로 확인한다. benchmark가 없다는 결과는 N/A receipt로
   남긴다.
4. `buildSrc`의 `timefoldSolver`와 alias가 실제 참조되지 않음을 `rg`와
   buildSrc compile로 먼저 확인한다. 미사용이면 삭제해 단일 catalog/BOM
   소유권을 만든다. 참조가 발견되면 삭제하지 않고 versionless catalog alias로
   이전하는 별도 변경으로 분리한다.
5. Spring Boot 3.5.13 애플리케이션에서 Timefold 2.6 starter가 끌어오는
   Boot 4.1.1/Jackson 3 메타데이터와의 coexistence를 dependency insight,
   compile, context smoke로 입증한다. 해결 실패 시 버전 임의 고정 대신
   중앙 소유권/호환성 결함으로 기록하고 구현을 중지한다.

### 5.2 school/bed 증분 점수 회귀

- 각 quickstart에 작은 fixture builder를 둔다. fixture는 충돌이 없는 상태,
  join이 생성되는 상태, join이 빠지는 상태, filter가 `true`에서 `false`로
  전환되는 상태를 각각 명시한다.
- `FULL_ASSERT` solver 환경에서 동일 fixture를 한 번에 풀고, 계획 변수 하나를
  이동한 재계산 결과와 기대 점수/constraint match를 비교한다. 모든 객체의
  입력 순서와 ID를 고정해 flaky한 시간·random seed 의존을 없앤다.
- `forEachUniquePair`, `join`, `filter`, `forEachIncludingUnassigned`가
  사용되는 실제 constraint를 대상으로 한다. true/false 경계가 없는
  constraint는 별도 회귀 대상으로 만들지 않고 이유를 기록한다.
- 내부 implementation API를 호출하지 않는다. 공개 solver/test API와
  `EnvironmentMode.FULL_ASSERT`를 사용하며, 정적 `ConstraintVerifier`는
  rule expectation 보조 증거로만 남긴다.
- 실패 시 constraint 수정은 RED가 실제 버그를 재현할 때만 허용한다. 재현되지
  않는 기존 코드의 “개선”은 수행하지 않는다.

### 5.3 `SolverManager` 수명주기

현재 school controller는 `jobIdToJob`에 best solution 또는 exception을
저장하고 `solverManager.solveBuilder().run()`을 호출한다. 다음 계약을
고정한다.

1. `solve` 호출은 immutable unique `jobId`로 한 번만 제출된다.
2. best-solution consumer는 중간 관찰값만 기록하며 완료 신호로 취급하지
   않는다.
3. `withFinalBestSolutionEventConsumer`는 정상 완료 또는 early termination의
   최종 결과를 정확히 한 번 기록한다.
4. `withExceptionHandler`는 예외 완료를 정확히 한 번 기록하고, 같은 job의
   정상 완료와 동시에 기록되지 않는다.
5. `terminateEarly` 후 status와 결과 조회가 race 없이 수렴한다. 종료된 job을
   다시 제출하거나 완료 결과를 중복 생성하지 않는다.
6. completion 상태는 `pending → completed` 또는 `pending → failed`의 단일
   전이로 제한한다. best/final/exception callback 순서가 뒤섞여도 원자적
   전이에서 이미 종료된 job은 후속 callback이 덮어쓰지 않는다.
7. `jobId`는 서버가 생성한 immutable unique 값이며, 요청 값이나 로그에
   비밀·전체 입력 payload를 넣지 않는다.
8. controller의 기존 HTTP 계약을 불필요하게 바꾸지 않고, 테스트에서
   callback 횟수·최종 결과·예외 결과를 독립적으로 관찰한다.

테스트는 실제 `SolverManager` builder를 사용한다. `CompletableFuture` 또는
`latch`는 테스트 관찰용으로만 사용하고, production 상태 저장소에 새
동시성 abstraction을 추가하지 않는다.

### 5.4 planning-list element와 declarative shadow

`00-shared/bluetape4k-timefold`에 다음 최소 모델을 추가한다.

- `ListShadowRoute`: `@PlanningEntity`이며 `@PlanningListVariable`인
  `visits`를 가진다.
- `ListShadowVisit`: `@PlanningEntity`로 등록한다. route의 list를 source로
  하는 `@IndexShadowVariable(sourceVariableName = "visits")`와, index에서
  파생되는 nullable `Int` custom shadow를 가진다.
- custom shadow supplier는 `@ShadowVariable(supplierName = "...")`와
  `@ShadowSources("indexInRoute")`를 사용한다. supplier는 순수하고
  결정적이며, 모든 planning source를 명시한다. 계산 중 필드를 변경하지
  않는다.
- `ListShadowPlan`은 route와 visit collection, visit value range,
  `@PlanningScore`를 갖고 solver가 두 planning entity 종류를 발견하도록
  명시적으로 등록한다.
- route와 visit collection은 서로 다른 `@PlanningEntityCollectionProperty`로
  등록하고, visit collection을 `@ValueRangeProvider`로 연결한다. list의
  소유권은 route가 가지며, visit의 planning list element shadow는 nullable
  wrapper 타입으로 노출한다. solver가 관리하는 list를 외부 caller가
  무단으로 공유·변경하지 않도록 fixture factory가 경계를 담당한다.

테스트 순서는 다음과 같다.

1. 세 visit을 한 route에 넣고 `SolutionManager.updateShadowVariables`를
   호출해 index/derived position이 `1, 2, 3`인지 확인한다.
2. 원본 list 순서를 `C, A, B`로 바꾸고 같은 update를 호출해 `1, 2, 3`이
   새 순서에 맞춰 `C, A, B`로 재계산되는지 확인한다.
3. visit을 다른 route로 이동하거나 미할당 상태로 만들고 nullable shadow가
   null/새 index로 수렴하는지 확인한다.
4. solver 재계획 fixture에서 동일 결과를 확인하고, 누락된 `@ShadowSources`나
   supplier side effect가 없음을 정적 검토한다.

`@PreviousElementShadowVariable` 등 built-in shadow를 사용한다면 source
variable 이름과 nullable first/last 경계를 별도 검증한다. 이번 최소 예제는
index 기반 custom shadow로 list reorder의 원인과 파생값을 한 단계로 보여준다.

### 5.5 Exposed persistence와 문서

- JDBC/R2DBC score round-trip 테스트를 Timefold 2.6 해결 그래프에서 순차
  실행하고, R2DBC H2 `1.1.0.RELEASE`와 H2 `2.4.240` ABI 호환성 고정을
  보존한다.
- root `README.md`와 `README.ko.md`에 2.6 그래프, list-shadow 예제 실행,
  lifecycle/증분 검증 명령을 같은 구조로 추가한다.
- `00-shared/bluetape4k-timefold/README.md`,
  `01-quickstarts/school-timetabling/README.md`/`README.adoc`,
  `01-quickstarts/bed-allocation/README.md`/`README.adoc`는 해당 예제의
  model annotation, 실행 명령, Enterprise/N/A 경계를 root 한영 문서와
  같은 의미로 맞춘다. 기존 locale 파일이 없는 모듈에는 새 locale 파일을
  만들지 않고 root의 한영 설명을 source of truth로 삼는다. 문서가 실제
  동작과 달라지면 구현보다 문서 정합성을 먼저 고친다.

## 6. 불변식과 실패 처리

| 위험/실패 | 탐지 | 처리 |
|---|---|---|
| 중앙 BOM과 local version hard-code 충돌 | catalog/graph/`rg` 및 effective model | local 버전 추가를 중단하고 중앙 alias/BOM 소유권으로 되돌림 |
| 2.3/2.4/2.5 migration 행 누락 | migration ledger source 검토 | 해당 행을 채우기 전 구현·merge 금지 |
| join/filter 증분 점수 stale 또는 중복 | FULL_ASSERT와 true/false transition fixture | RED를 보존하고 최소 constraint/domain 수정 후 재검증 |
| best/final/exception callback 유실·중복 | callback counter와 final result latch | production 상태 변경을 최소화하고 단일 completion transition으로 수렴 |
| shadow source 미선언·재배치 stale | source mutation/reorder 테스트와 API descriptor 오류 | 모든 의존성을 `@ShadowSources`에 명시, supplier side effect 제거 |
| Boot 3/Timefold 2.6 classpath 충돌 | dependency insight, compile, application context | 임의 exclude/version override 금지; 호환성 gap을 P1 blocker로 기록 |
| Exposed JDBC/R2DBC round-trip drift | sequential persistence tests | 원인 module만 수정하고 H2/R2DBC ABI pin 보존 |
| Enterprise API를 community 검증으로 오인 | disabled test와 artifact graph 확인 | `SKIPPED`를 PASS로 보고하지 않고 N/A/보류로 명시 |

## 7. 검증 순서와 통과 기준

검증은 의존 순서대로 수행한다.

```bash
./gradlew :bluetape4k-timefold:test
./gradlew :school-timetabling:test :bed-allocation:test
./gradlew :exposed:jdbc-examples:test :exposed:r2dbc-examples:test
./gradlew test
./gradlew build
scripts/verify-publication-poms.py --workspace .. --summary
git diff --check
```

추가로 다음을 보관한다.

- `dependencies`/`dependencyInsight`의 exact-head 출력
- 각 migration source URL과 해당 버전 영향 표
- 증분 fixture의 입력·기대 score·실제 score
- lifecycle callback count와 early/exception 결과
- shadow update 전후 list와 파생값
- Exposed 테스트 결과와 README 한영 parity diff

`SKIPPED`, `NO-SOURCE`, Enterprise disabled test, benchmark 부재는 PASS가
아니다. 실행 불가 항목은 명령·원인·대체 증거를 함께 기록한다.

## 8. Issue #54 acceptance traceability

| Issue 요구 | 구현/증거 산출물 | PASS 조건 |
|---|---|---|
| 1. 버전과 2.2→2.6 migration | dependency graph + migration ledger | 각 중간 버전 행이 공식 source와 local 영향으로 채워짐 |
| 2. 중앙 소유권/BOM | catalog diff + resolved graph + POM 검증 | local hard-code 없음, 선언/해결 일치 |
| 3. school/bed join/filter | deterministic incremental tests | join entry/exit와 filter transition 모두 기대 score |
| 4. SolverManager lifecycle | controller lifecycle tests | final/early/exception 각각 단일 완료 |
| 5. list element shadow | shared model/test | mutation/reorder/미할당 shadow refresh |
| 6. Neighborhoods | `rg` N/A receipt 및 문서 | 사용 없음의 구체적 근거, custom move 없음 |
| 7. Exposed와 README | sequential tests + `README.md`/`README.ko.md` | persistence PASS와 한영 실행 문서 일치 |

## 9. 롤백과 재실행 경계

- 모든 구현은 `feat/issue-54-timefold-2.6-verification`에서만 수행한다.
- 중앙 BOM, default branch, 기존 dirty/active worktree는 건드리지 않는다.
- 특정 구성 요소가 실패하면 해당 component의 테스트와 변경만 되돌리고,
  migration/graph 근거를 보존한 채 다음 component로 원인을 확장하지 않는다.
- 공개 API나 module topology 변경이 필요해지면 현재 명세를 중단하고 별도
  Type A 설계 승인으로 분리한다.
- merge, remote branch 삭제, release/tag, snapshot publish는 이 명세의
  실행 결과만으로 자동 수행하지 않으며 exact-head 재검토와 별도 승인을
  요구한다.

## 10. 참고한 공식 자료

- [Timefold Solver upgrade overview](https://docs.timefold.ai/timefold-solver/latest/upgrading-timefold-solver/overview)
- [Upgrade from v1 to v2](https://docs.timefold.ai/timefold-solver/latest/upgrading-timefold-solver/upgrade-from-v1)
- [Backwards compatibility](https://docs.timefold.ai/timefold-solver/latest/upgrading-timefold-solver/backwards-compatibility)
- [Modeling planning problems](https://docs.timefold.ai/timefold-solver/latest/domain-modeling/modeling-planning-problems)
- [SolverManager library integration](https://docs.timefold.ai/timefold-solver/latest/running-timefold-solver/library/library-integration)
- [Chained variables to planning list variable](https://docs.timefold.ai/timefold-solver/latest/upgrading-timefold-solver/migration-guides/chained-variables-to-planning-list-variable)
- [Variable listeners to custom shadow variables](https://docs.timefold.ai/timefold-solver/latest/upgrading-timefold-solver/migration-guides/variable-listeners-to-custom-shadow-variables)
- [Commercial editions](https://docs.timefold.ai/timefold-solver/latest/commercial-editions/commercial-editions)

## 11. 작성 검증

- SPW-01 목적·대상 독자·성공 조건: PASS — 유지보수자와 학습자 모두를
  대상으로 범위와 acceptance를 분리했다.
- SPW-02 용어·구조·명령 정확성: PASS — API/identifier/command/URL은 원문
  토큰을 보존하고 prose는 한국어로 작성했다.
- SPW-03 한국어 자연스러움: PASS — 중복 주어, 번역투 연결어, 과도한
  명사열을 줄이고 기술 식별자만 English로 유지했다.
- SPW-04 근거 추적성: PASS — 기준 SHA, fresh 명령, 공식 문서 URL, N/A
  경계를 각 설계 항목에 연결했다.
- SPW-05 독립 검토 준비: PASS — 7-Tier 검토 artifact에서 성능·안정성·보안·
  운영·API·사용자·통합 관점을 별도로 판정한다.
