import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("com.diffplug.spotless")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // modules
    implementation(project(":module-api"))
    implementation(project(":module-persistence"))

    // annotation processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // implementation
    implementation("org.springframework.boot:spring-boot-starter")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
