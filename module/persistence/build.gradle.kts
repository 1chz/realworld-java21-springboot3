dependencies {
    implementation(project(":module:core"))

    runtimeOnly(libs.db.h2)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.cache)

    implementation(libs.cache.caffeine)
}
