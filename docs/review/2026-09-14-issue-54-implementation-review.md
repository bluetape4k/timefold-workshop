# Issue #54 Timefold Solver 2.6.0 구현 7-Tier 검토

검토일: 2026-09-14 (Asia/Seoul)
검토 기준 HEAD: `9b0466ea276251b547c7b0afb8982dafa7e4424e`
기준 브랜치: `origin/develop` (`db724ee0f4c848770169651d5851188ae608db3c`)
대상 브랜치: `feat/issue-54-timefold-2.6-verification`
검토 방식: 주 세션 통합 검토(inline fallback)

이 문서는 정확한 branch diff, 현재 소스 line anchor, fresh test receipt를
대조한 결과다. 별도 native reviewer의 독립 attestation으로 간주하지 않는다.
P0/P1은 발견하지 않았으며, 범위 밖 또는 실행 환경에 한정된 P2/P3는 아래
표에 disposition을 남겼다.

## 1. 변경 범위와 요구사항 추적

`origin/develop...HEAD`의 변경은 중앙 BOM이나 workflow를 편집하지 않고 다음
범위에 머문다.

- Timefold 2.2→2.6 migration ledger와 versionless catalog 근거
  (`docs/research/2026-09-14-timefold-2.6-migration.md`)
- school/bed FULL_ASSERT join·filter 회귀 fixture
- school `TimetableJobRegistry`와 SolverManager final/failure lifecycle
- shared `@PlanningListVariable` element와 declarative shadow fixture
- Exposed persistence 실행 증거와 root/기존 module README 정합성
- 실행 계획·설계·review/lesson 증적

Issue #54의 일곱 acceptance row는 migration 문서와 승인된 plan의
traceability 표에 연결되어 있다. 새 module, dependency, central BOM, release,
merge는 추가하지 않았다.

## 2. 7-Tier 관점별 판정

| 관점 | 근거(line anchor 포함) | finding | 우선순위 | disposition |
|---|---|---|---|---|
| 성능 | `TimetableIncrementalScoreTest.kt:34-99`, `BedAllocationIncrementalScoreTest.kt:37-84`가 `FULL_ASSERT`, 고정 seed, `stepCountLimit=64`를 사용한다. shared fixture는 `ListShadowDomainTest.kt:64-86`에서 한 step을 실행한다. | correctness fixture는 짧고 결정론적이나 benchmark 소비·throughput 목표는 없다. runtime graph에도 benchmark가 없다. | P2 | Issue 범위 밖/ N/A로 기록. 별도 성능 최적화는 Type F로 분리한다. |
| 안정성 | `TimetableJobRegistry.kt:22-87`의 entry별 `ReentrantLock`, `PENDING → COMPLETED|FAILED` 단일 전이와 `TimetableJobRegistryTest.kt:25-95`의 callback 순서·동시성 검증. | terminal state 이후 late callback을 차단한다. 완료 entry retention/TTL은 line 29 TODO로 남아 있다. | P2 | 기존 HTTP 계약에 retention 정책이 없어 후속 API 설계로 보류. P0/P1 없음. |
| 보안 | `TimetableController.kt:51-76`은 request payload를 로그에 남기지 않고, line 53의 server-generated UUID와 line 74-76의 callback ID만 기록한다. | secret/payload 노출과 caller-supplied job ID 의존이 없다. | 없음 | PASS. 내부 `ScoreDirector` 구현 API도 사용하지 않는다. |
| 운영/ops | `docs/research/2026-09-14-timefold-2.6-migration.md`의 Task 6 receipt는 Colima/Docker 상태, R2DBC daemon 첫 실패, `--no-daemon --max-workers=1` 대체 실행을 보존한다. | 첫 전체 publication-POM 실행은 `bluetape4k-projects`의 기존 dirty 변경 때문에 clean-worktree guard에서 중단됐다. clean publisher 8곳은 `110 POM`, `17,566 dependencies`, Maven model `110`, failures `0`이다. | P2 | dirty `bluetape4k-projects`를 건드리지 않고 보존. 전체 publisher gate는 해당 저장소가 clean해진 뒤 재실행한다. |
| 개발자/API | `ListShadowDomain.kt:24-76`이 public planning annotations와 nullable shadow를 사용한다. controller는 `withFinalBestSolutionEventConsumer`를 `TimetableController.kt:58-81`에 연결한다. root catalog alias는 versionless이고 central BOM이 2.6.0을 관리한다. | public API·기존 module topology를 재사용했다. 잘못된 중첩 Gradle 경로를 실제 `:exposed-jdbc-examples`/`:exposed-r2dbc-examples`로 정정했다. | 없음 | PASS. 새 dependency/module과 local Timefold hard-code 없음. |
| 사용자/호출자 | school README의 endpoint 표와 lifecycle 명령, bed README의 REST controller 부재 설명, root `README.md`/`README.ko.md`의 동일 검증 명령을 확인했다. Enterprise `ScoreAnalysis`는 disabled/N/A로 명시한다. | reader-facing 실행 경로는 현재 Spring Boot/WebFlux 코드와 일치한다. AsciiDoc renderer 자체는 실행하지 않았다. | P2 | `git diff --check`와 경로/키워드 검사는 PASS. renderer는 별도 문서 도구가 있는 환경에서 후속 확인한다. |
| main integration | `./gradlew test`와 `./gradlew build`가 root에서 exit 0. root test XML 집계는 `159 tests`, failures/errors `0`, Enterprise disabled `1`. `git diff --check` PASS. | 모듈·root integration에 P0/P1 결함이 없다. Gradle deprecated-feature/Netty restricted-method 경고는 기존 환경 경고다. | 없음 | PASS. 경고를 새 회귀의 PASS로 세지 않는다. |

## 3. 검증 영수증

모든 Gradle 검증은 feature worktree에서 `--no-daemon --max-workers=1
--no-build-cache`로 순차 실행했다.

| 명령 | 결과 |
|---|---|
| `:bluetape4k-timefold:test` | `5 tests`, failures/errors/skipped `0`, `BUILD SUCCESSFUL` |
| `:school-timetabling:test` | `41 tests`, failures/errors `0`, Enterprise disabled `1`, exit `0` |
| `:bed-allocation:test` | `41 tests`, failures/errors/skipped `0`, exit `0` |
| `:exposed-jdbc-examples:test` | `48 tests`, failures/errors/skipped `0`, `BUILD SUCCESSFUL` |
| `:exposed-r2dbc-examples:test` | `24 tests`, failures/errors/skipped `0`, `BUILD SUCCESSFUL` |
| root `test` | `159 tests`, failures/errors `0`, disabled `1`, `BUILD SUCCESSFUL in 23s` |
| root `build` | exit `0`, `BUILD SUCCESSFUL in 25s` |
| publication POM (clean 8 publishers) | failures `0`, POM `110`, dependencies `17,566`, Maven models `110` |
| `git diff --check` 및 docs path/keyword scan | PASS |

R2DBC의 최초 daemon 종료는 테스트 실행 전 `compileTestKotlin`에서 발생했고,
daemon log에는 사용자 중단에 따른 정상 종료만 있었다. 동일 명령을 no-daemon
단일 worker로 재실행해 compile과 전체 suite가 모두 PASS했으므로 코드 결함으로
분류하지 않는다. `bluetape4k-projects`의 dirty 상태 때문에 전체 9-publisher
publication gate를 성공으로 주장하지 않으며, clean 8-publisher 결과만
검증 증거로 사용한다.

## 4. P0/P1 통합 판정

- P0: `0`
- P1: `0`
- P2: benchmark/운영 지표 부재(범위 밖), registry retention 정책 미정,
  dirty publisher guard, AsciiDoc renderer 미실행
- P3: 없음

P2 항목은 현재 acceptance를 막지 않으며 각각 별도 범위·환경 증거로
기록했다. Enterprise `ScoreAnalysis` disabled, benchmark 부재,
Neighborhoods/custom move 미사용은 PASS가 아니라 명시적 N/A다.

## 5. Writer gate (SPW-01~05)

- SPW-01 목적·독자·성공 조건: PASS — Issue acceptance와 검증 결과를 연결했다.
- SPW-02 용어·구조·명령 정확성: PASS — 실제 Gradle project path, API 이름,
  test count, exit 상태를 보존했다.
- SPW-03 한국어 자연스러움: PASS — reader-facing 설명과 판정은 한국어로
  작성하고 code/API/command token은 원문을 유지했다.
- SPW-04 근거 추적성: PASS — exact HEAD, file:line, receipt, central BOM,
  공식 migration 문서 링크를 연결했다.
- SPW-05 독립 검토 준비: PASS — 7개 관점, P0~P3, N/A와 known gap을
  독립 재검토 가능한 형태로 남겼다(독립 attestation 자체는 아님).

## 결론

Issue #54 구현 diff는 승인된 Type A 범위에서 P0/P1 없이 검증됐다. 로컬
feature branch의 구현·문서·테스트 증적은 완료 상태이며, dirty
`bluetape4k-projects`를 포함한 전체 publisher 재검증과 remote push/PR/merge는
별도 exact-head 및 권한 gate로 남긴다.
