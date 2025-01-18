dependencies {
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Remove Jakarta Persistence API related warnings
    implementation("jakarta.persistence:jakarta.persistence-api")
}
