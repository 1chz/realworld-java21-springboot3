plugins {
    java
    id("com.diffplug.spotless")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // modules
    compileOnly(project(":module-core"))

    // implementation
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // test implementation
    testImplementation("org.springframework.security:spring-security-test")
}
