plugins {
    java
    id("com.diffplug.spotless")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // modules
    implementation(project(":module-core"))

    // runtime only
    runtimeOnly("com.h2database:h2")

    // implementation
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
}
