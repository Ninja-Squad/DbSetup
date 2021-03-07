import java.time.Duration

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
}

group = "com.ninja-squad"

nexusPublishing {
    repositories {
        sonatype {
            username.set(project.findProperty("sonatypeUsername")?.toString() ?: "")
            password.set(project.findProperty("sonatypePassword")?.toString() ?: "")
        }
    }
    connectTimeout.set(Duration.ofMinutes(3))
    clientTimeout.set(Duration.ofMinutes(3))
}
