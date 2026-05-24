dependencies {
    implementation(libs.timefold.solver.core)

    // JetBrains Exposed core (version managed by jetbrains-exposed-bom)
    implementation(libs.jetbrains.exposed.jdbc)
    implementation(libs.jetbrains.exposed.dao)

    // bluetape4k-exposed extensions (version managed by bluetape4k-dependencies BOM)
    implementation(libs.exposed.dao)
    testImplementation(libs.exposed.jdbc.tests)

    // Timefold Solver의 Score 에 대해 Exposed 용 Custom Column Types 제공
    implementation(libs.bluetape4k.timefold.solver.persistence.exposed)

    testImplementation(libs.bluetape4k.io)
    testImplementation(libs.bluetape4k.coroutines)
    testImplementation(libs.bluetape4k.junit5)

    testImplementation(libs.hikaricp)

    testImplementation(libs.h2)
    testImplementation(libs.mariadb.java.client)
    testImplementation(libs.mysql.connector.j)
    testImplementation(libs.postgresql.driver)
    testImplementation(libs.pgjdbc.ng)

    testImplementation(libs.bluetape4k.testcontainers)
    testImplementation(libs.testcontainers.lib)
    testImplementation(libs.testcontainers.mariadb)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.testcontainers.postgresql)
}
