rootProject.name = "realworld"

pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val spotlessVersion: String by settings

    plugins {
        java
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("com.diffplug.spotless") version spotlessVersion
    }
}

listOf("bootstrap", "core", "api", "persistence").forEach { moduleName ->
    include(moduleName)
    project(":$moduleName").projectDir = initializeModule(moduleName)
}

fun initializeModule(moduleName: String): File {
    val packages = "sample/shirohoo/realworld"
    return file(moduleName).let { module ->
        module.mkdirs()
        createBuildScript(module)
        file("$module/src/main/java/$packages").mkdirs()
        file("$module/src/test/java/$packages").mkdirs()
        module // return module directory
    }
}

fun createBuildScript(module: File) {
    module.resolve("build.gradle.kts").let {
        if (it.exists()) {
            return
        }
        it.writeText(
            """
            plugins {
                java
                id("com.diffplug.spotless")
                id("org.springframework.boot")
                id("io.spring.dependency-management")
            }

            dependencies {
                implementation("org.springframework.boot:spring-boot-starter")
                testImplementation("org.springframework.boot:spring-boot-starter-test")
            }
            """.trimIndent(),
        )
    }
}
