# school-timetabling

Kotlin/Spring WebFlux로 수업을 교실과 시간대에 배정하는 Timefold Solver
quickstart입니다. Timefold Solver `2.6.0`은 중앙
`bluetape4k-dependencies` BOM에서 관리합니다.

## 사전 요구 사항

- Java 25+
- Docker와 Testcontainers를 실행할 수 있는 환경(테스트 시)

## 애플리케이션 실행

저장소 루트에서 다음 명령을 실행합니다.

```bash
./gradlew :school-timetabling:bootRun
```

기본 주소는 <http://localhost:8080>입니다. 데모 문제를 조회한 뒤
`POST /timetables`로 풀이를 제출합니다.

| Method | Path | 설명 |
|---|---|---|
| `GET` | `/demo-data` | 사용 가능한 `DataSizeType` 목록 |
| `GET` | `/demo-data/{dataSizeType}` | 데모 timetable 생성 |
| `GET` | `/timetables` | 등록된 solver job ID 목록 |
| `POST` | `/timetables` | timetable 풀이 제출, job ID 반환 |
| `GET` | `/timetables/{jobId}/status` | 점수와 solver 상태 조회 |
| `GET` | `/timetables/{jobId}` | 최종 timetable 조회 |
| `DELETE` | `/timetables/{jobId}` | 조기 종료 후 결과 조회 |

`TimetableJobRegistry`가 best/final/failure callback을 기록하고 terminal
상태를 보호합니다. 증분 join/filter와 SolverManager lifecycle 회귀는 다음
명령으로 검증합니다.

```bash
./gradlew :school-timetabling:test --tests '*TimetableIncrementalScoreTest'
./gradlew :school-timetabling:test --tests '*TimetableJobRegistryTest'
./gradlew :school-timetabling:test --tests '*TimetableControllerTest'
```

`PUT /timetables/analyze`는 Community Edition에서 Enterprise 기능이므로
테스트가 disabled/N/A입니다. Enterprise 자격 증명을 로컬 설정에 추가하지
않아도 나머지 예제를 실행할 수 있습니다.

## 참고 자료

- [Timefold Solver 2.6.0 release](https://github.com/TimefoldAI/timefold-solver/releases/tag/v2.6.0)
- [Spring Boot integration](https://docs.timefold.ai/timefold-solver/latest/running-timefold-solver/library/spring-boot)
