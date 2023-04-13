plugins {
    java
    jacoco
    id("com.diffplug.spotless") version "6.18.0"
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "io.github.shirohoo"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
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

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    runtimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        html.required.set(true)
        html.outputLocation.set(file("$buildDir/jacoco/html"))
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
        importOrder("io.github.shirohoo", "java", "javax", "jakarta", "org", "com", "lombok")
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
