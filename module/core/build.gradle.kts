plugins {
    `java-test-fixtures`
}

dependencies {
    implementation(libs.jakarta.persistence.api)
    testFixturesImplementation(libs.jakarta.persistence.api)
}
