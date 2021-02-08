plugins {
    `java-library-convention`
}

project.description = "Helps you setup your database with test data"

java {
    withJavadocJar()
}

dependencies {
    compileOnly("com.google.code.findbugs:jsr305:2.0.0")
    compileOnly("net.sourceforge.findbugs:annotations:1.3.2")

    testImplementation("junit:junit:4.+")
    testImplementation("org.mockito:mockito-all:1.9.0")
    testImplementation("org.hsqldb:hsqldb:2.3.3")
}

tasks {
    javadoc {
        (options as StandardJavadocDocletOptions).apply {
            overview(file("src/main/java/com/ninja_squad/dbsetup/overview.html").path)
            noTimestamp(true)
            linkSource(true)
            addBooleanOption("Xdoclint:all,-missing", true)
        }
    }
}
