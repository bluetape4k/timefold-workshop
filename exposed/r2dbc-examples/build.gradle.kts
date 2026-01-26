dependencies {
    implementation(Libs.timefold_solver_bom)

    implementation(Libs.timefold_solver_core)
    implementation(Libs.timefold_solver_persistence_common)
    testImplementation(Libs.timefold_solver_test)

    implementation(Libs.exposed_r2dbc)
    implementation(Libs.bluetape4k_exposed_r2dbc)
    testImplementation(Libs.bluetape4k_exposed_r2dbc_tests)

    // Timefold Solver의 Score 에 대해 Exposed 용 Custom Column Types 제공
    implementation(Libs.bluetape4k_timefold_solver_persistence_exposed)

    implementation(Libs.bluetape4k_io)
    implementation(Libs.bluetape4k_coroutines)
    testImplementation(Libs.bluetape4k_junit5)

    testImplementation(Libs.r2dbc_pool)
    testImplementation(Libs.r2dbc_h2)
    testImplementation(Libs.r2dbc_mariadb)
    testImplementation(Libs.r2dbc_mysql)
    testImplementation(Libs.r2dbc_postgresql)

    testImplementation(Libs.bluetape4k_testcontainers)
    testImplementation(Libs.testcontainers)
    testImplementation(Libs.testcontainers_mariadb)
    testImplementation(Libs.testcontainers_mysql)
    testImplementation(Libs.testcontainers_postgresql)

    // Testcontainers 용 DB 서버 실행 시 Driver를 필요로 합니다.
    testRuntimeOnly(Libs.mariadb_java_client)
    testRuntimeOnly(Libs.mysql_connector_j)
    testRuntimeOnly(Libs.postgresql_driver)
}
