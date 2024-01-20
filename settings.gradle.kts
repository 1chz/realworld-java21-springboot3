rootProject.name = "realworld"

include("modular-application", "module-core", "module-api", "module-persistence")

pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val spotlessVersion: String by settings

    plugins {
        java
        jacoco
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("com.diffplug.spotless") version spotlessVersion
    }
}
