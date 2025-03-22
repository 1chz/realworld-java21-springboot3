import org.springframework.boot.gradle.tasks.bundling.BootJar

val javaVersion = libs.versions.java.get()

plugins {
    java
    alias(libs.plugins.spotless)
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
}

allprojects {
    group = "io.zhc1"

    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")

    repositories {
        mavenCentral()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }

    spotless {
        java {
            palantirJavaFormat("2.38.0")
            indentWithSpaces()
            formatAnnotations()
            removeUnusedImports()
            trimTrailingWhitespace()
            importOrder("java", "jakarta", "org", "com", "net", "io", "lombok", "io.zhc1")
        }

        kotlin {
            ktlint()
            indentWithSpaces()
            trimTrailingWhitespace()
        }

        kotlinGradle {
            ktlint()
            indentWithSpaces()
            trimTrailingWhitespace()
        }
    }
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    configurations {
        all { exclude(group = "junit", module = "junit") }
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        implementation("org.springframework.boot:spring-boot-starter")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.getByName<BootJar>("bootJar") {
        enabled = false
    }

    tasks.getByName<Jar>("jar") {
        enabled = true
    }
}
