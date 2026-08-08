## 배경

Timefold Exposed 예제 모듈에 JetBrains Exposed Gradle plugin을 도입했다.

## 결정

workshop은 관리되는 `bt4k` catalog과 독립적으로 유지한다. 공식 Exposed plugin이
해당 라인에서 제공되므로 로컬 JetBrains Exposed BOM 라인을 1.3.0으로 옮겼다.

## 결과

JDBC 및 R2DBC Exposed 예제 모듈이 이제 plugin을 적용하고
`generateMigrations`를 노출한다.

## 검증

`git diff --check`, `./gradlew -q help`,
`:exposed-jdbc-examples:tasks --all`을 실행했다.

## 후속 보호 규칙

workshop plugin 버전을 `bluetape4k-dependencies` catalog 참조에 묶지 않는다.
저장소가 관리되는 library repo가 되기 전까지 로컬 alias를 명시적으로 유지한다.
