dependencies {
    implementation(Libs.timefold_solver_bom)

    implementation(Libs.timefold_solver_core)
    implementation(Libs.timefold_solver_persistence_common)
    testImplementation(Libs.timefold_solver_test)

    implementation(Libs.exposed_r2dbc)
    implementation(libs.exposed.r2dbc)
    testImplementation(libs.exposed.r2dbc.tests)

    // Timefold Solver의 Score 에 대해 Exposed 용 Custom Column Types 제공
    implementation(libs.bluetape4k.timefold.solver.persistence.exposed)

    implementation(libs.bluetape4k.io)
    implementation(libs.bluetape4k.coroutines)
    testImplementation(libs.bluetape4k.junit5)

    testImplementation(Libs.r2dbc_pool)
    testImplementation(Libs.r2dbc_h2)
    testImplementation(Libs.r2dbc_mariadb)
    testImplementation(Libs.r2dbc_mysql)
    testImplementation(Libs.r2dbc_postgresql)

    testImplementation(libs.bluetape4k.testcontainers)
    testImplementation(Libs.testcontainers)
    testImplementation(Libs.testcontainers_mariadb)
    testImplementation(Libs.testcontainers_mysql)
    testImplementation(Libs.testcontainers_postgresql)

    // Testcontainers 용 DB 서버 실행 시 Driver를 필요로 합니다.
    testRuntimeOnly(Libs.mariadb_java_client)
    testRuntimeOnly(Libs.mysql_connector_j)
    testRuntimeOnly(Libs.postgresql_driver)
}
