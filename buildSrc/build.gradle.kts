plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("de.marcphilipp.gradle:nexus-publish-plugin:0.4.0")
    implementation("io.codearte.gradle.nexus:gradle-nexus-staging-plugin:0.22.0")
}
