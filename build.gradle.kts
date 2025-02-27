import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


val ktor_version = "2.3.0"


plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.1.0"
    id("io.ktor.plugin") version "2.3.0"
    kotlin("plugin.serialization") version "1.8.10" // Ensure it matches your Kotlin version

}

tasks.withType<ShadowJar> {
    archiveBaseName.set("my-ktor-app")
    archiveClassifier.set("")
    archiveVersion.set("")
    manifest {
        attributes["Main-Class"] = "org.example.MainKt" // Ensure this matches your main function
    }
}

application {
    mainClass.set("org.example.MainKt")  // Replace with your actual main class path
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "org.example.MainKt"  // Ensure this matches your main function
    }
}


group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    implementation("ch.qos.logback:logback-classic:1.4.14")


    implementation("ch.qos.logback:logback-classic:1.4.12") // Logback for logging
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    // data base
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.xerial:sqlite-jdbc:3.40.1.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
    implementation("com.zaxxer:HikariCP:5.0.1") // Connection pool

}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.test {
    useJUnitPlatform()
}



kotlin {
    jvmToolchain(17)
}