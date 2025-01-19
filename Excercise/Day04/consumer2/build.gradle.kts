plugins {
    kotlin("jvm") version "1.9.22" // Or latest Kotlin version
    id("com.github.johnrengelman.shadow") version "7.1.2" // Add Shadow plugin
    application
}

group = "com.example" // Replace with your group
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.quartz-scheduler:quartz:2.3.2") // Or latest Quartz version
    implementation("com.typesafe:config:1.4.3") // For configuration
    implementation("org.slf4j:slf4j-simple:2.0.9") // Simple logging implementation
    implementation("ch.qos.logback:logback-classic:1.2.6")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

application {
    mainClass.set("com.example.MainKt")  // This should match your Main.kt's package and class
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.example.MainKt" // Replace with your actual main class
        )
    }
}

tasks {
    shadowJar {
        archiveBaseName.set("my-kotlin-app")
        archiveVersion.set("1.0")
        mergeServiceFiles() // To handle service files properly
    }
}