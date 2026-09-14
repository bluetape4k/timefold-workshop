# bluetape4k-timefold 공통 라이브러리

Timefold Solver 예제에서 재사용하는 Kotlin 공통 API와 테스트 fixture를
제공합니다. 이 모듈은 애플리케이션을 실행하지 않으며, 독립적으로 컴파일과
테스트할 수 있습니다.

## Planning-list shadow 예제

`io.bluetape4k.timefold.listshadow` 패키지는 다음 public annotation 조합을
작은 도메인으로 고정합니다.

- `@PlanningListVariable`: route가 visit의 순서와 소유를 관리합니다.
- `@IndexShadowVariable`: visit의 현재 route index를 계산합니다.
- `@ShadowVariable` + `@ShadowSources("indexInRoute")`: index에서 파생된
  sequence position을 선언적으로 갱신합니다.

shadow 값을 직접 쓰지 말고 Timefold의 `SolutionManager.updateShadowVariables`
또는 solver lifecycle을 사용합니다. 회귀 검증은 저장소 루트에서 실행합니다.

```bash
./gradlew :bluetape4k-timefold:test --tests '*ListShadowDomainTest'
```
