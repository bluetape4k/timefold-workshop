# Dependencies 1.2.0 동기화

## 배경

최종 upstream BOM matrix가 Maven Central에서 확인 가능해진 뒤
`bluetape4k-dependencies:1.2.0`이 공개되었다.

## 결정

Timefold workshop 공유 catalog을 `1.1.4`에서 `1.2.0`으로 옮긴다.

## 결과

workshop 예제가 이제 공개된 1.2.0 의존성 거버넌스 기준을 사용한다.

## 검증

- `sync-shared-versions.py --workspace .. --write --check --summary`로 catalog
  라인을 갱신했다.
- Maven Central에서
  `io.github.bluetape4k:bluetape4k-dependencies:1.2.0`에 HTTP 200을 반환했다.
