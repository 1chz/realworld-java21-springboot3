springBoot {
    mainClass.set("sample.shirohoo.realworld.RealworldApplication")
}

plugins {
    java
    jacoco
    id("com.diffplug.spotless")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // modules
    implementation(project(":module-api"))
    implementation(project(":module-persistence"))

    // annotation processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // implementation
    implementation("org.springframework.boot:spring-boot-starter")

    // test implementation
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "junit", module = "junit")
    }
}
