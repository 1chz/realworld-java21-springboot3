plugins {
    java
    id("com.diffplug.spotless")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // implementation
    implementation("jakarta.persistence:jakarta.persistence-api") // for JPA annotations
    implementation("org.springframework.boot:spring-boot-starter")
}
