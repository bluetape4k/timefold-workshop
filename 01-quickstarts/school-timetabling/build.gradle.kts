plugins {
    kotlin("plugin.spring")
    id(Plugins.spring_boot)
    id(Plugins.graalvm_native)
}

springBoot {
    mainClass.set("timefold.workshop.school.timetabling.SchoolTimetablingApplicationKt")
}

@Suppress("UnstableApiUsage")
configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-timefold"))

    implementation(libs.timefold.solver.core)
    implementation(libs.timefold.solver.jackson)
    implementation(libs.timefold.solver.spring.boot.starter)
    testImplementation(libs.timefold.solver.test)

    // Bluetape4k
    implementation(libs.bluetape4k.jackson2)
    implementation(libs.bluetape4k.idgenerators)
    implementation(libs.bluetape4k.io)
    implementation(libs.bluetape4k.spring.boot3.core)
    testImplementation(libs.bluetape4k.junit5)
    testImplementation(libs.bluetape4k.testcontainers)

    // Coroutines
    implementation(libs.bluetape4k.coroutines)
    implementation(libs.kotlinx.coroutines.reactor)
    testImplementation(libs.kotlinx.coroutines.test)

    // Reactor
    implementation(libs.reactor.kotlin.extensions)

    // Spring Boot
    implementation(libs.spring.boot.autoconfigure)
    annotationProcessor(libs.spring.boot.autoconfigure.processor)
    annotationProcessor(libs.spring.boot.configuration.processor)
    runtimeOnly(libs.spring.boot.devtools)

    implementation(libs.spring.boot.starter.aop)
    implementation(libs.spring.boot.starter.webflux)
    testImplementation(libs.spring.boot.starter.test) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
}
