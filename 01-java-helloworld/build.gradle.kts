plugins {
    java
    application
}

dependencies {
    compile("org.protelis:protelis:12.1.0")
    compile("org.jgrapht:jgrapht-core:1.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testCompile("org.mockito:mockito-core:2.1.0")
    testCompile("org.mockito:mockito-junit-jupiter:2.23.0")
}

application {
    mainClassName = "demo.HelloProtelis"
}

tasks.test {
    useJUnitPlatform()
}

defaultTasks("run")
