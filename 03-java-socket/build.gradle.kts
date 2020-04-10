plugins {
    kotlin("jvm")
}
dependencies {
    implementation(project(":01-java-helloworld"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.konf)
}

application {
    mainClassName = "demo.HelloProtelis"
}

defaultTasks("run")
