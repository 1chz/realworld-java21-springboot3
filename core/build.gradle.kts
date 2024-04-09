plugins {
    java
    `java-test-fixtures`
    id("com.diffplug.spotless")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("jakarta.persistence:jakarta.persistence-api")
}
