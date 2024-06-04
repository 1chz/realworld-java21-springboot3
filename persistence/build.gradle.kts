dependencies {
    implementation(project(":core"))

    runtimeOnly("com.h2database:h2")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
}
