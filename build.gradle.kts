plugins {
    java
    jacoco
    id("com.diffplug.spotless") version "6.18.0"
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
}

group = "sample.shirohoo"
java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // annotation processor
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // compile only
    compileOnly("org.projectlombok:lombok")

    // runtime only
    runtimeOnly("com.h2database:h2")

    // implementation
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")

    // test implementation
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "junit", module = "junit")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        html.required.set(true)
        html.outputLocation.set(file("$layout.buildDirectory/jacoco/html"))
    }
}

tasks.compileJava {
    dependsOn(tasks.clean)
}

spotless {
    java {
        palantirJavaFormat()
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
