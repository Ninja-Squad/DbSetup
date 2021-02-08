import java.time.Duration

plugins {
    `java-library-convention`
    kotlin("jvm") version "1.4.20"
    id("org.jetbrains.dokka") version "1.4.20"
}

repositories {
    // TODO replace this by other repo once dokka allows it
    jcenter()
}

project.description = "Kotlin extensions for DbSetup"

dependencies {
    api(project(":DbSetup"))
    testImplementation("junit:junit:4.+")
    testImplementation("org.mockito:mockito-all:1.9.0")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveFileName.set("dokka-html.jar")
    from(tasks.dokkaJavadoc)
}

val dokkaJar by tasks.registering(Jar::class) {
    archiveFileName.set("dokka-javadoc.jar")
    from(tasks.dokkaHtml)
}

publishing {
    publications.named<MavenPublication>("maven") {
        artifact(dokkaJar) {
            classifier = "dokka"
        }
        artifact(javadocJar) {
            classifier = "javadoc"
        }
    }
}
