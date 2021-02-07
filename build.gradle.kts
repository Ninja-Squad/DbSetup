plugins {
    id("io.codearte.nexus-staging")
    id("de.marcphilipp.nexus-publish")
}

group = "com.ninja-squad"

nexusStaging {
    username = sonatypeUsername
    password = sonatypePassword
    repositoryDescription = "Release ${project.group} ${project.version}"
}

nexusPublishing {
    repositories {
        sonatype()
    }
    connectTimeout.set(java.time.Duration.ofMinutes(3))
    clientTimeout.set(java.time.Duration.ofMinutes(3))
}
