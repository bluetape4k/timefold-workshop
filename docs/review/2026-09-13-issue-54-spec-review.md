# Issue #54 설계 명세 7-Tier 검토

검토일: 2026-09-13
대상 명세: `docs/superpowers/specs/2026-09-13-issue-54-timefold-2.6-design.md`
명세 SHA-256: `75b745d5285420ba5d107a180605169f3850ec315a200d0a37f94502f18efc52`
기준 코드 HEAD: `db724ee0f4c848770169651d5851188ae608db3c`
검토 방식: 주 세션 inline fallback review

이번 검토는 현재 실행 지침에 따라 별도 native reviewer를 새로 기동하지
않고 주 세션에서 수행했다. 따라서 아래 결과는 독립 attestation이 아니라
명세·공식 Timefold 문서·현재 코드 근거를 다시 읽은 통합 검토 evidence다.

## 검토 범위

- 설계 대안, 범위/제외 경계, 중앙 BOM 소유권
- Timefold 2.6 dependency graph와 2.2→2.6 migration ledger 의무
- school/bed 증분 score fixture
- `SolverManager` final/early/exception completion과 원자적 상태 전이
- `@PlanningListVariable` element와 declarative shadow source 경로
- Exposed JDBC/R2DBC, 한영 README, rollback와 N/A 경계

## 여섯 관점 결과

| 우선순위 | 관점 | 근거와 판정 | 필요한 조치 | 상태 |
|---|---|---|---|---|
| P2 | 성능 | 명세 5.2가 작은 결정론적 fixture와 `FULL_ASSERT`를 요구하지만 별도 benchmark 목표는 없다. Issue #54는 benchmark 기능 검증이 아니며 runtime graph에 benchmark가 없다는 fresh N/A 증거가 있다. | fixture 크기와 실행 시간을 결과에 기록하고, 별도 성능 최적화는 Type F로 분리한다. | 보류(범위 외, 위험 수용) |
| P0/P1 없음 | 안정성 | 명세 5.3의 `pending → completed/failed` 단일 전이, final/exception exclusivity, early termination 수렴이 callback race를 직접 다룬다. | 구현 시 callback counter와 latch를 반드시 검증한다. | PASS |
| P0/P1 없음 | 보안 | 명세 5.3에서 server-generated immutable `jobId`, payload/secret 비로깅, 내부 API 미사용을 고정했다. 입력은 기존 controller 경계를 넘지 않는다. | 로그와 테스트에서 실제 payload/secret이 없는지 확인한다. | PASS |
| P2 | 운영 | 교육용 workshop이므로 metrics/production rollout은 범위 밖이다. 명세 9의 rollback, 중앙 BOM 보존, exact-head/별도 merge gate는 포함되어 있다. | README에 실패 경로와 재실행 명령을 남기고 production observability는 별도 운영 작업으로 분리한다. | 보류(제품 운영 범위 외) |
| P0/P1 없음 | 개발자/API | versionless catalog alias, shared module 재사용, `@PlanningEntity` 등록, nullable shadow, public API만 사용하도록 명시했다. 새 module·custom move·internal `ScoreDirector`를 금지한다. | buildSrc 미사용 alias는 참조 검색과 compile 후에만 삭제한다. | PASS |
| P0/P1 없음 | 사용자/호출자 | root와 기존 module README의 한영 parity, annotation/실행 예제, Enterprise/N/A 경계를 명세 5.5와 acceptance 표에 연결했다. | 구현 후 README 명령을 실제 실행해 read-back한다. | PASS |

## 통합 판정

1. 요구사항 1~7이 명세 1, 5, 8의 acceptance traceability와 검증 명령으로
   연결되어 있다.
2. 중앙 BOM 재편집, 새 module, Neighborhoods preview, Enterprise API라는
   범위 확장 경계가 명시되어 있어 승인된 계획과 모순되지 않는다.
3. P0=0, P1=0이다. P2 두 건은 workshop 교육 범위와 Issue #54의
   benchmark/운영 범위를 넘으므로 결과 기록·후속 분리로 처리한다.
4. migration ledger의 각 중간 버전 source와 실제 출력 채우기는 구현 단계의
   필수 evidence이며, 현재 설계 단계에서 완료되었다고 주장하지 않는다.
5. unresolved user decision은 없다. 승인된 실행 계획을 변경하는 P0/P1
   수정도 없다.

## 설계 명세 writer gate (SPW-01~05)

- SPW-01 목적·독자·성공 조건: PASS — target, acceptance, 교육/유지보수
  독자를 명시했다.
- SPW-02 용어·구조·명령 정확성: PASS — Kotlin/Timefold API 식별자와 명령,
  SHA, URL을 그대로 보존했다.
- SPW-03 한국어 자연스러움: PASS — prose는 한국어로 정리하고 code/API
  token만 English로 유지했다.
- SPW-04 근거 추적성: PASS — 기준 HEAD, dependency 명령, 공식 docs URL,
  N/A 경계를 연결했다.
- SPW-05 독립 검토 준비: PASS — 여섯 관점과 통합 판정, P0/P1/P2 처리를
  별도 표로 남겼다.

## 결론

`A-03 Step 2-R`는 PASS 조건(P0=0, P1=0)을 충족한다. 명세를 materially
바꾸는 추가 결정은 없으며, 다음 단계는 이 명세에 따른 ordered implementation
plan 작성과 그 계획의 7-Tier 검토다.
