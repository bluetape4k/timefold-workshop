# timefold-workshop

[English](README.md) | [한국어](README.ko.md)

![Timefold workshop 작업대](./docs/assets/timefold-workbench.png)

[Timefold Solver](https://timefold.ai/)의 제약 모델링, 스케줄 최적화,
점수 계산, 영속성 연동을 Kotlin/Spring 예제로 학습하는 워크숍 저장소입니다.

## 프로젝트 목적

`timefold-workshop`은 스케줄링 시스템에서 자주 등장하는 최적화 문제를 실행
가능한 예제로 다룹니다. 시간대 배정, 이벤트 충돌 회피, 리소스 제약 준수,
소프트 선호도 균형 조정 같은 문제를 Timefold Solver로 모델링합니다.

## 제공 기능

- **Quickstart 예제** - Timefold planning 핵심 개념을 빠르게 확인합니다.
- **스케줄링 도메인 모델** - 예약, 시간대, 리소스, 제약 조건을 모델링합니다.
- **공통 bluetape4k 헬퍼** - Kotlin 친화적인 Timefold 테스트와 예제를 지원합니다.
- **Exposed 영속성 예제** - JDBC/R2DBC 기반 solver application 구성을 보여줍니다.
- **제약 중심 테스트** - 스케줄링 동작을 테스트로 검증합니다.

## 아키텍처

![timefold workshop Architecture diagram](docs/assets/readme-diagrams/timefold-workshop-architecture-01.png)

<!-- README_VISUAL_OVERVIEW:START -->
## Overview Diagram

![Timefold Workshop overview diagram](docs/assets/readme-diagrams/root-readme-overview-01.png)

## Module Composition Chart

![Timefold Workshop module composition chart](docs/assets/readme-charts/root-readme-module-chart-01.png)
<!-- README_VISUAL_OVERVIEW:END -->

## 모듈

| 모듈 | 역할 |
|---|---|
| `bluetape4k-timefold` | 공통 Timefold helper API와 테스트 유틸리티. |
| `school-timetabling` | 수업을 교실과 시간대에 배정하는 quickstart. |
| `bed-allocation` | 제한된 병상을 제약 조건에 맞게 배정하는 quickstart. |
| `exposed-jdbc-examples` | Solver-backed service를 위한 Exposed JDBC 영속성 예제. |
| `exposed-r2dbc-examples` | Reactive solver-backed service를 위한 Exposed R2DBC 영속성 예제. |

## 환경

- Java 25+
- Kotlin 2.3+
- Spring Boot 3.5+
- Timefold Solver
- Kotlin Exposed 영속성 예제

## 빌드

```bash
./gradlew build
./gradlew test
./gradlew :school-timetabling:test
./gradlew :bed-allocation:test
```

## Timefold Solver 2.6.0 검증

Timefold 버전은 중앙 `bluetape4k-dependencies` BOM이 소유합니다.
`gradle/libs.versions.toml`의 `libs.timefold.solver.*` alias는 versionless로
유지하므로 각 모듈의 `timefold-solver-core`, Jackson 연동, Spring Boot
starter가 로컬 버전 재정의 없이 `2.6.0`으로 해결됩니다.

저장소 루트에서 Issue 전용 회귀 검증을 실행합니다.

```bash
./gradlew :bluetape4k-timefold:test --tests '*ListShadowDomainTest'
./gradlew :school-timetabling:test --tests '*TimetableIncrementalScoreTest'
./gradlew :school-timetabling:test --tests '*TimetableJobRegistryTest'
./gradlew :bed-allocation:test --tests '*BedAllocationIncrementalScoreTest'
./gradlew :exposed-jdbc-examples:test
./gradlew :exposed-r2dbc-examples:test
```

공통 fixture는 `@PlanningListVariable`, `@IndexShadowVariable`, 선언형
`@ShadowVariable` 갱신을 검증합니다. School과 bed fixture는 join/filter 점수
전이를 확인하고, `TimetableJobRegistry`는 SolverManager의 best/final/failure
callback을 하나의 terminal state로 수렴시킵니다. Community 빌드에서는
Enterprise `ScoreAnalysis` endpoint 테스트를 disabled 상태로 유지하며,
Enterprise 자격 증명이 없어 N/A로 보고합니다. 이 워크숍에는 활성 source
surface가 없으므로 Timefold 2.6 Neighborhoods와 custom-move API를 도입하지
않습니다.

영속성 예제는 별도 Gradle project입니다. Testcontainers를 사용하는 경우
JDBC와 R2DBC 테스트를 한 번에 하나씩 실행합니다. R2DBC 컴파일 중 Gradle
daemon이 종료되면 `--no-daemon --max-workers=1`을 추가해 재시도할 수
있습니다. 이 옵션은 process 격리만 바꾸며 저장소 설정은 변경하지 않습니다.

## 참고 자료

- [timefold.ai](https://timefold.ai/) - Timefold 공식 웹사이트
- [timefold-solver](https://github.com/TimefoldAI/timefold-solver) - Timefold Solver
- [timefold-quickstarts](https://github.com/TimefoldAI/timefold-quickstarts) - Timefold Quickstarts
