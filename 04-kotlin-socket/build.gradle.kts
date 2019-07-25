import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.40"
    application
}

dependencies {
    implementation(kotlin("stdlib"))
    compile(project(":02-kotlin-helloworld"))
    compile("org.protelis:protelis:12.1.0")
    compile("com.uchuhimo:konf:0.13.3")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
    testImplementation("io.mockk:mockk:1.9.1")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClassName = "demo.HelloProtelisKt"
}
