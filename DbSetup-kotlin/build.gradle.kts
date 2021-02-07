import java.time.Duration

plugins {
    `java-library-convention`
    kotlin("jvm") version "1.4.20"
}

project.description = "Kotlin extensions for DbSetup"

dependencies {
    api(project(":DbSetup"))
    testImplementation("junit:junit:4.+")
    testImplementation("org.mockito:mockito-all:1.9.0")
}
