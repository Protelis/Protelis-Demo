plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    compile(project(":01-java-helloworld"))
    compile("com.uchuhimo:konf:0.13.3")
}

application {
    mainClassName = "demo.HelloProtelis"
}

defaultTasks("run")
