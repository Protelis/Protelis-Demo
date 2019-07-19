plugins {
    kotlin("jvm") version "1.3.40"
    application
}

dependencies {
    implementation(kotlin("stdlib"))
    compile("org.protelis:protelis:12.1.0")
    compile("org.jgrapht:jgrapht-core:1.3.1")
}

application {
    mainClassName = "demo.HelloProtelisKt"
}