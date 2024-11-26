import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val jdkVersion = 17

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(jdkVersion))
    }
    // Or shorter:
    jvmToolchain(jdkVersion)
}

dependencies {
    implementation(project(mapOf("path" to ":nanohttpdrouter")))
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.21")
    implementation("org.nanohttpd:nanohttpd:2.3.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}
