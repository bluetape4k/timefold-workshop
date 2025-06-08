plugins {
    kotlin("plugin.spring")
    id(Plugins.spring_boot)
    id(Plugins.graalvm_native)
}

springBoot {
    mainClass.set("timefold.workshop.bed.allocation.BedAllocationApplicationKt")
}

@Suppress("UnstableApiUsage")
configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-timefold"))

    implementation(Libs.timefold_solver_core)
    implementation(Libs.timefold_solver_jackson)
    implementation(Libs.timefold_solver_spring_boot_starter)
    testImplementation(Libs.timefold_solver_test)

    // Bluetape4k
    implementation(Libs.bluetape4k_jackson)
    implementation(Libs.bluetape4k_idgenerators)
    implementation(Libs.bluetape4k_io)
    implementation(Libs.bluetape4k_spring_webflux)
    implementation(Libs.bluetape4k_javatimes)
    testImplementation(Libs.bluetape4k_spring_tests)
    testImplementation(Libs.bluetape4k_junit5)
    testImplementation(Libs.bluetape4k_testcontainers)

    // Coroutines
    implementation(Libs.bluetape4k_coroutines)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    implementation(Libs.reactor_kotlin_extensions)

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    annotationProcessor(Libs.springBoot("autoconfigure-processor"))
    annotationProcessor(Libs.springBoot("configuration-processor"))
    runtimeOnly(Libs.springBoot("devtools"))

    implementation(Libs.springBootStarter("aop"))
    implementation(Libs.springBootStarter("webflux"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
}
