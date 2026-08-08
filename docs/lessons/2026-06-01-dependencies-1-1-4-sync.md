# Dependencies 1.1.4 동기화

## 배경

`bluetape4k-dependencies` 1.2.0 릴리스 준비를 위해 BOM 릴리스 CI를 통과하기
전에 하위 workshop이 중앙 공유 버전 원천과 일치해야 했다.

## 결정

workshop catalog을 최신 공개 기준인 `bluetape4k-dependencies:1.1.4`에 맞춘다.
`1.2.0`이 공개될 때까지는 사용하지 않는다.

## 결과

중앙 릴리스 preflight에서 workshop의 공유 버전 drift가 더 이상 보고되지 않는다.

## 검증

`bluetape4k-dependencies`에서
`sync-shared-versions.py --workspace /Users/debop/work/bluetape4k --write --check --summary`로
검증했다.
