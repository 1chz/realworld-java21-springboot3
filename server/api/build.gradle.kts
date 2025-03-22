import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation(project(":module:core"))
    implementation(project(":module:persistence"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Remove Jakarta Persistence API related warnings
    implementation("jakarta.persistence:jakarta.persistence-api")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
