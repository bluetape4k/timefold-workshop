dependencies {
    api(libs.timefold.solver.core)
    testImplementation(libs.timefold.solver.test)

    api(libs.bluetape4k.io)
    api(libs.bluetape4k.coroutines)
    testImplementation(libs.bluetape4k.junit5)
}
