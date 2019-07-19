plugins {
    kotlin("jvm") version "1.3.40"
    java
    application
}

dependencies {
    implementation(kotlin("stdlib"))
    compile(project(":01-java-helloworld"))
    compile("org.protelis:protelis:12.1.0")
    compile("com.uchuhimo:konf:0.13.3")
}


application {
    mainClassName = "demo.HelloProtelis"
}

defaultTasks("run")
