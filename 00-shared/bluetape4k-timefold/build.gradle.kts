dependencies {
    implementation(Libs.timefold_solver_bom)
    
    api(Libs.timefold_solver_core)
    testImplementation(Libs.timefold_solver_test)

    api(libs.bluetape4k.io)
    api(libs.bluetape4k.coroutines)
    testImplementation(libs.bluetape4k.junit5)
}
