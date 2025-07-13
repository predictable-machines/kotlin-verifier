plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.kotlin.verifier") version "0.1.0-SNAPSHOT"
}

// add coroutines dependencies
dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}
