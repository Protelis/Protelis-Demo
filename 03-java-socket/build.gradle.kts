dependencies {
    implementation(project(":01-java-helloworld"))
    implementation(libs.konf)
}

application {
    mainClassName = "demo.HelloProtelis"
}

defaultTasks("run")
