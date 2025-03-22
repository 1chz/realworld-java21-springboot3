dependencies {
    implementation(project(":module:core"))

    runtimeOnly("com.h2database:h2")

    implementation(libs.spring.boot.starter.p6spy)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
