# Issue #54 Implementation Plan 7-Tier Review

검토일: 2026-09-14
대상 계획: docs/superpowers/plans/2026-09-14-issue-54-timefold-2.6-plan.md
계획 SHA-256: 69bdd9eb425ec4395fd647a9989a734636fb8bdf3d804329e05c759303e93f0a
기준 코드 HEAD: db724ee0f4c848770169651d5851188ae608db3c
검토 방식: 주 세션 inline fallback review

별도 native reviewer를 새로 기동하지 않고 현재 지침에 따라 주 세션에서
검토했다. 따라서 이 문서는 독립 attestation이 아니라 계획·설계·현재
repository anchors를 재검토한 통합 evidence다.

## Required plan checks

| Priority | Area | Finding | Required plan edit | Status |
|---|---|---|---|---|
| P0/P1 없음 | Coverage | Issue #54의 7개 acceptance가 Task 1~6에 매핑되고 Task 7이 DoD/lesson/review를 담당한다. | 없음 | PASS |
| P0/P1 없음 | Ordering | graph/ownership → RED tests → minimal implementation → docs → full verification 순서다. | 없음 | PASS |
| P0/P1 없음 | Testability | school/bed join/filter, lifecycle success/failure/concurrency, shadow reorder/move, JDBC/R2DBC backend를 각각 명명했다. | 없음 | PASS |
| P0/P1 없음 | Docs/API | root와 기존 module README, Korean prose, public Timefold API와 central catalog ownership을 task에 포함했다. | 없음 | PASS |
| P0/P1 없음 | Hazards | new module/dependency, Spring Boot coexistence, Exposed H2 ABI, Enterprise N/A, Testcontainers sequential rule을 명시했다. | 없음 | PASS |
| P0/P1 없음 | Rollback | central BOM/release/merge/publication을 제외하고 component별 rerun/stop 조건을 제시했다. | 없음 | PASS |

## 여섯 관점

| 우선순위 | 관점 | 근거와 판정 | 처리 |
|---|---|---|---|
| P2 | 성능 | FULL_ASSERT fixture는 작게 제한했지만 별도 benchmark는 없다. benchmark는 현재 runtime graph에 없고 Issue #54의 correctness 범위 밖이다. | 결과에 fixture 크기·duration을 기록하고 Type F로 분리 |
| P0/P1 없음 | 안정성 | registry 단일 terminal state, callback 순서, early termination, duplicate/concurrent cases를 Task 4에서 테스트한다. entry별 ReentrantLock 같은 명시적 primitive를 사용하고, Exposed/Testcontainers는 순차 실행한다. | PASS |
| P0/P1 없음 | 보안 | server-generated jobId, payload/secret 비로깅, 내부 Timefold implementation API 미사용을 Task 4와 5에 고정했다. | PASS |
| P2 | 운영 | workshop에는 production metrics/rollout이 없지만 rollback, exact-head, N/A와 재실행 command가 있다. | README와 lesson에 운영 범위 밖임을 기록 |
| P0/P1 없음 | 개발자/API | exact file ownership, Kotlin patterns, versionless alias, public API, nullable shadow, no new module을 명시했다. registry snapshot도 mutable Entry를 노출하지 않는다. | PASS |
| P0/P1 없음 | 사용자/호출자 | annotation/실행 command, Enterprise disabled 경계, 한영 parity와 unsupported Neighborhoods를 문서 task에 연결했다. | PASS |

## Main-session integration

1. Task 2는 새 solver를 상태별로 재생성하는 방식에서 한 번의 deterministic
   FULL_ASSERT run으로 증분 guard를 수행하도록 수정했다. controlled match
   count는 ConstraintVerifier 보조 증거로만 남긴다.
2. Task 4 registry snippet에는 EntrySnapshot, recordBest, recordFinal,
   recordFailure, get을 모두 포함해 controller wiring과 타입 이름이 일치한다.
3. Task 5는 route/visit 두 planning entity collection과 visit value range를
   명시해 list element discovery 누락을 방지한다.
4. Task 1은 2.3/2.4/2.5/2.6 각 공식 source를 구현 전 채우도록 하며,
   graph와 central BOM 변경을 혼동하지 않는다.
5. coroutine cancellation은 production coroutine boundary를 새로 만들지
   않고 기존 suspend endpoint와 SolverManager callback을 유지하므로 별도
   cancellation task는 N/A다. callback 종료·예외·early termination은
   동시성 테스트로 대체 증명한다.
6. P0=0, P1=0이다. P2 두 건은 명시적 범위 밖으로 보류했고, 구현 결과의
   duration과 README/lesson 기록은 필수다. unresolved user decision은 없다.

## Plan writer gate (SPW-01~05)

- SPW-01 목적·독자·성공 조건: PASS — Goal, architecture, acceptance mapping이 있다.
- SPW-02 구조·용어·명령: PASS — exact paths, Kotlin symbols, Gradle commands,
  expected outcomes를 적었다.
- SPW-03 한국어 자연스러움: PASS — 독자-facing prose는 한국어이며 technical
  identifiers만 원문을 유지했다.
- SPW-04 근거 추적성: PASS — approved spec, baseline SHA, graph command,
  official migration ledger, rollback을 연결했다.
- SPW-05 실행 가능성: PASS — checkbox task, RED/GREEN 순서, full verification,
  stop conditions와 no-placeholder self-review를 포함한다.

## 결론

A-04 Step 3-R는 P0=0/P1=0으로 PASS한다. 계획 자체를 materially 바꾸는
미해결 finding은 없다. 다음은 승인된 spec/plan을 feature branch에 commit한
뒤 Task 1부터 TDD로 실행하는 것이다.
