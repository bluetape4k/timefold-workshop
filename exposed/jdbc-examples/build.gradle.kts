dependencies {
    implementation(Libs.timefold_solver_bom)
    api(Libs.timefold_solver_core)
    api(Libs.timefold_solver_persistence_common)
    testImplementation(Libs.timefold_solver_test)

    implementation(project(":bluetape4k-timefold"))

    api(Libs.exposed_jdbc)
    api(Libs.bluetape4k_exposed)
    testImplementation(Libs.bluetape4k_exposed_tests)

    implementation(Libs.bluetape4k_io)
    implementation(Libs.bluetape4k_coroutines)
    testImplementation(Libs.bluetape4k_junit5)

    testImplementation(Libs.h2_v2)
    testImplementation(Libs.mariadb_java_client)
    testImplementation(Libs.mysql_connector_j)
    testImplementation(Libs.postgresql_driver)

    testImplementation(Libs.bluetape4k_testcontainers)
    testImplementation(Libs.testcontainers_junit_jupiter)
    testImplementation(Libs.testcontainers_mariadb)
    testImplementation(Libs.testcontainers_mysql)
    testImplementation(Libs.testcontainers_postgresql)
}
