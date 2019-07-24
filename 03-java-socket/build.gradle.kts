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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testCompile("org.mockito:mockito-core:2.1.0")
    testCompile("org.mockito:mockito-junit-jupiter:2.23.0")
}


application {
    mainClassName = "demo.HelloProtelis"
}

defaultTasks("run")
