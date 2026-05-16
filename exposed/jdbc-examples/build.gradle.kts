dependencies {
    implementation(Libs.timefold_solver_bom)

    implementation(Libs.timefold_solver_core)
    implementation(Libs.timefold_solver_persistence_common)
    testImplementation(Libs.timefold_solver_test)

    implementation(Libs.exposed_jdbc)
    implementation(Libs.exposed_dao)
    implementation(libs.exposed.dao)

    // Timefold Solver의 Score 에 대해 Exposed 용 Custom Column Types 제공
    implementation(libs.bluetape4k.timefold.solver.persistence.exposed)

    testImplementation(libs.exposed.jdbc.tests)

    testImplementation(libs.bluetape4k.io)
    testImplementation(libs.bluetape4k.coroutines)
    testImplementation(libs.bluetape4k.junit5)

    testImplementation(Libs.hikaricp)

    testImplementation(Libs.h2_v2)
    testImplementation(Libs.mariadb_java_client)
    testImplementation(Libs.mysql_connector_j)
    testImplementation(Libs.postgresql_driver)
    testImplementation(Libs.pgjdbc_ng)

    testImplementation(libs.bluetape4k.testcontainers)
    testImplementation(Libs.testcontainers)
    testImplementation(Libs.testcontainers_mariadb)
    testImplementation(Libs.testcontainers_mysql)
    testImplementation(Libs.testcontainers_postgresql)
}
