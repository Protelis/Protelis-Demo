dependencies {
    implementation(project(":01-java-helloworld"))
    implementation(libs.konf)
    implementation(libs.paho)
    implementation(libs.moquette.broker)
}

application {
    mainClass.set("demo.HelloProtelis")
}
