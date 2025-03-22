plugins {
    `java-test-fixtures`
}

dependencies {
    implementation("jakarta.persistence:jakarta.persistence-api")
    testFixturesImplementation("jakarta.persistence:jakarta.persistence-api")

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
}
