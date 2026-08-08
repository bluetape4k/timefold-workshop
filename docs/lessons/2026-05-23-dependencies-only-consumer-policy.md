# Dependencies 전용 소비자 정책

## 배경

Timefold workshop이 bluetape4k artifact 버전을 직접 고정하고 있었다. 릴리스
업그레이드 중 현재 릴리스 라인에 없는 오래된
`bluetape4k-spring-boot3-core` 좌표가 드러났다.

## 결정

`bluetape4k-dependencies`를 유일한 bluetape4k 버전 원천으로 사용하고 bluetape4k
artifact는 버전 없이 선언한다. 과거 accessor 이름은 BOM이 관리하는 유효한
좌표를 가리킬 때만 유지한다.

## 결과

catalog이 `bluetape4k-dependencies`를 import하고 bluetape4k 직접 버전 참조를
제거했다. Spring Boot core alias는 현재 `bluetape4k-spring-boot-core` artifact를
가리킨다.

## 검증

금지 참조 grep, `git diff --check`,
`./gradlew compileKotlin --no-daemon --no-configuration-cache`를 실행했다.

## 후속 지침

BOM 업그레이드 후 bluetape4k artifact를 resolve할 수 없다면 로컬 버전 override를
추가하기 전에 artifact 좌표가 바뀌었는지 확인한다.
