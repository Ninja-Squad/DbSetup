import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-library`
    signing
    `maven-publish`
    jacoco
}

group = rootProject.group
version = rootProject.version

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

repositories {
    mavenCentral()
}

tasks {
    val checkJavaVersion by registering  {
        doLast {
            if (!JavaVersion.current().isJava8()) {
                val message = "ERROR: Java 1.8 required but ${JavaVersion.current()} found. Change your JAVA_HOME environment variable.";
                throw IllegalStateException(message);
            }
        }
    }

    compileJava {
        dependsOn(checkJavaVersion)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Jar> {
        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "ninja-squad.com",
                "Bundle-Vendor" to "ninja-squad.com"
            )
        }
    }

    test {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    check {
        dependsOn(jacocoTestReport)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set(project.provider(Callable { project.description }))
                url.set("https://dbsetup.ninja-squad.com/")
                organization {
                    name.set("Ninja Squad")
                    url.set("https://ninja-squad.com")
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://dbsetup.ninja-squad.com/license.html")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("jnizet")
                        name.set("Jean-Baptiste Nizet")
                        email.set("jb@ninja-squad.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Ninja-Squad/DbSetup")
                    developerConnection.set("scm:git:git://github.com/Ninja-Squad/DbSetup")
                    url.set("https://github.com/Ninja-Squad/DbSetup")
                }
            }
        }
    }
    repositories {
        maven {
            name = "build"
            url = uri("${rootProject.buildDir}/repo")
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}


