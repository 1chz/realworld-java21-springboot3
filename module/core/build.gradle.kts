plugins {
    `java-test-fixtures`
}

dependencies {
    implementation(libs.jakarta.persistence.api)
    testFixturesImplementation(libs.jakarta.persistence.api)

    implementation(libs.spring.boot.starter.cache)
    implementation(libs.cache.caffeine)
}
