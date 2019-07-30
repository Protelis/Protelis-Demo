plugins {
    kotlin("jvm")
}

dependencies {
    implementation(Libs.kotlin_stdlib)
    compile(project(":01-java-helloworld"))
    compile(Libs.konf)
}

application {
    mainClassName = "demo.HelloProtelis"
}

defaultTasks("run")
