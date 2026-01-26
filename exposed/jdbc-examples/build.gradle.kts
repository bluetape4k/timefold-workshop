dependencies {
    implementation(Libs.timefold_solver_bom)

    implementation(Libs.timefold_solver_core)
    implementation(Libs.timefold_solver_persistence_common)
    testImplementation(Libs.timefold_solver_test)

    implementation(Libs.exposed_jdbc)
    implementation(Libs.exposed_dao)
    implementation(Libs.bluetape4k_exposed)

    // Timefold Solver의 Score 에 대해 Exposed 용 Custom Column Types 제공
    implementation(Libs.bluetape4k_timefold_solver_persistence_exposed)

    testImplementation(Libs.bluetape4k_exposed_tests)

    testImplementation(Libs.bluetape4k_io)
    testImplementation(Libs.bluetape4k_coroutines)
    testImplementation(Libs.bluetape4k_junit5)

    testImplementation(Libs.hikaricp)

    testImplementation(Libs.h2_v2)
    testImplementation(Libs.mariadb_java_client)
    testImplementation(Libs.mysql_connector_j)
    testImplementation(Libs.postgresql_driver)
    testImplementation(Libs.pgjdbc_ng)

    testImplementation(Libs.bluetape4k_testcontainers)
    testImplementation(Libs.testcontainers)
    testImplementation(Libs.testcontainers_mariadb)
    testImplementation(Libs.testcontainers_mysql)
    testImplementation(Libs.testcontainers_postgresql)
}
