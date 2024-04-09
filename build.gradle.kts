import org.springframework.boot.gradle.tasks.bundling.BootJar

group = "sample.shirohoo"

springBoot {
    mainClass.set("sample.shirohoo.realworld.RealWorldApplication")
}

plugins {
    java
    id("com.diffplug.spotless")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    configurations {
        all { exclude(group = "junit", module = "junit") }
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    repositories {
        mavenCentral()
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

    spotless {
        java {
            palantirJavaFormat("2.38.0")
            indentWithSpaces()
            formatAnnotations()
            removeUnusedImports()
            trimTrailingWhitespace()
            importOrder("java", "jakarta", "org", "com", "net", "io", "lombok", "sample.shirohoo")
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
