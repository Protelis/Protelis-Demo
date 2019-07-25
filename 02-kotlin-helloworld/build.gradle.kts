import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.40"
    application
}

dependencies {
    implementation(kotlin("stdlib"))
    compile("org.protelis:protelis:12.1.0")
    compile("org.jgrapht:jgrapht-core:1.3.1")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
    testImplementation("io.mockk:mockk:1.9.1")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "demo.HelloProtelisKt"
}