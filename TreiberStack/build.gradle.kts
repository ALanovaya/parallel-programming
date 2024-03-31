plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "stack"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines:0.19.2")
    testImplementation("org.jetbrains.kotlinx:lincheck:2.26")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}