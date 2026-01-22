dependencies {
    implementation(Libs.timefold_solver_bom)
    api(Libs.timefold_solver_core)
    api(Libs.timefold_solver_persistence_common)
    testImplementation(Libs.timefold_solver_test)

    implementation(project(":bluetape4k-timefold"))

    api(Libs.exposed_r2dbc)
    api(Libs.bluetape4k_exposed_r2dbc)
    testImplementation(Libs.bluetape4k_exposed_r2dbc_tests)

    implementation(Libs.bluetape4k_io)
    implementation(Libs.bluetape4k_coroutines)
    testImplementation(Libs.bluetape4k_junit5)

    testImplementation(Libs.r2dbc_pool)
    testImplementation(Libs.r2dbc_h2)
    testImplementation(Libs.r2dbc_mariadb)
    testImplementation(Libs.r2dbc_mysql)
    testImplementation(Libs.r2dbc_postgresql)

    testImplementation(Libs.bluetape4k_testcontainers)
    testImplementation(Libs.testcontainers_junit_jupiter)
    testImplementation(Libs.testcontainers_mariadb)
    testImplementation(Libs.testcontainers_mysql)
    testImplementation(Libs.testcontainers_postgresql)
}
