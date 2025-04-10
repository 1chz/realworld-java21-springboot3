import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    alias(libs.plugins.spotless)
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
}

// Resolving the issue of not being able to reference the version catalog in allprojects and subprojects scopes
val versionCatalog = libs

allprojects {
    group = "io.zhc1"

    plugins.apply(
        versionCatalog.plugins.java
            .get()
            .pluginId,
    )
    plugins.apply(
        versionCatalog.plugins.spotless
            .get()
            .pluginId,
    )

    repositories {
        mavenCentral()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(versionCatalog.versions.java.get()))
        }
    }

    spotless {
        java {
            palantirJavaFormat().formatJavadoc(true)

            formatAnnotations()
            removeUnusedImports()
            trimTrailingWhitespace()
            importOrder("java", "jakarta", "org", "com", "net", "io", "lombok", "io.zhc1")
        }

        kotlin {
            ktlint()
            trimTrailingWhitespace()
        }

        kotlinGradle {
            ktlint()
            trimTrailingWhitespace()
        }
    }
}

subprojects {
    plugins.apply(
        versionCatalog.plugins.spring.boot
            .get()
            .pluginId,
    )
    plugins.apply(
        versionCatalog.plugins.spring.dependency.management
            .get()
            .pluginId,
    )

    configurations {
        all { exclude(group = "junit", module = "junit") }
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {
        implementation(platform(versionCatalog.spring.boot.bom))

        compileOnly(versionCatalog.lombok)
        annotationProcessor(versionCatalog.lombok)

        implementation(versionCatalog.spring.boot.starter)
        testImplementation(versionCatalog.spring.boot.starter.test)
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
