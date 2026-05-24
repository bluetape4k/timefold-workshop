dependencies {
    implementation(libs.timefold.solver.core)

    // JetBrains Exposed core (version managed by jetbrains-exposed-bom)
    implementation(libs.jetbrains.exposed.r2dbc)

    // bluetape4k-exposed extensions (version managed by bluetape4k-dependencies BOM)
    implementation(libs.exposed.r2dbc)
    testImplementation(libs.exposed.r2dbc.tests)

    // Timefold Solver의 Score 에 대해 Exposed 용 Custom Column Types 제공
    implementation(libs.bluetape4k.timefold.solver.persistence.exposed)

    implementation(libs.bluetape4k.io)
    implementation(libs.bluetape4k.coroutines)
    testImplementation(libs.bluetape4k.junit5)

    testImplementation(libs.r2dbc.pool)
    testImplementation(libs.r2dbc.h2)
    testImplementation(libs.r2dbc.mariadb)
    testImplementation(libs.r2dbc.mysql)
    testImplementation(libs.r2dbc.postgresql)

    testImplementation(libs.bluetape4k.testcontainers)
    testImplementation(libs.testcontainers.lib)
    testImplementation(libs.testcontainers.mariadb)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.testcontainers.postgresql)

    // Testcontainers 용 DB 서버 실행 시 Driver를 필요로 합니다.
    testRuntimeOnly(libs.mariadb.java.client)
    testRuntimeOnly(libs.mysql.connector.j)
    testRuntimeOnly(libs.postgresql.driver)
}
