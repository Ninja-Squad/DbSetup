import org.gradle.api.Project

val Project.sonatypeUsername
    get() = findProperty("sonatypeUsername")?.toString() ?: ""
val Project.sonatypePassword
    get() = findProperty("sonatypePassword")?.toString() ?: ""
