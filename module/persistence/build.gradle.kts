dependencies {
    implementation(project(":module:core"))

    runtimeOnly(libs.db.h2)

    implementation(libs.spring.boot.starter.p6spy)
    implementation(libs.spring.boot.starter.data.jpa)
}
