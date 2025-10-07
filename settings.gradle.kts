pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
    plugins {
        // https://plugins.gradle.org/plugin/org.gradle.toolchains.foojay-resolver-convention
        id("org.gradle.toolchains.foojay-resolver-convention") version ("0.10.0")
    }
}

val PROJECT_NAME = "timefold"

rootProject.name = "$PROJECT_NAME-workshop"

includeModules("exposed", withProjectName = false, withBaseDir = true)

includeModules("00-shared", withProjectName = false, withBaseDir = false)
includeModules("01-quickstarts", withProjectName = false, withBaseDir = false)

fun includeModules(baseDir: String, withProjectName: Boolean = true, withBaseDir: Boolean = true) {
    files("$rootDir/$baseDir").files
        .filter { it.isDirectory }
        .forEach { moduleDir ->
            moduleDir.listFiles()
                ?.filter { it.isDirectory }
                ?.forEach { dir ->
                    val basePath = baseDir.replace("/", "-")
                    val projectName = when {
                        !withProjectName && !withBaseDir -> dir.name
                        withProjectName && !withBaseDir -> PROJECT_NAME + "-" + dir.name
                        withProjectName -> PROJECT_NAME + "-" + basePath + "-" + dir.name
                        else -> basePath + "-" + dir.name
                    }
                    // println("include modules: $projectName")

                    include(projectName)
                    project(":$projectName").projectDir = dir
                }
        }
}
