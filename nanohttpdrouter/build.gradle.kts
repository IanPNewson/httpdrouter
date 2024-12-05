import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.8.21"
    `java-library`
}

group = "org.iannewson.httpdrouter"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.nanohttpd:nanohttpd:2.3.1")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.google.code.gson:gson:2.11.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<Jar>("jar") {
    from(sourceSets.main.get().output)
    archiveBaseName.set("nanohttprouter") // Set the desired JAR name
    archiveVersion.set("0.9") // Set the version
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
