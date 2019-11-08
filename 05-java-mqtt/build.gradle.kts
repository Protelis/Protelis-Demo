// moquette needs jcenter
repositories {
    jcenter()
}

dependencies {
    implementation(Libs.kotlin_stdlib)
    implementation(Libs.org_eclipse_paho_client_mqttv3)
    implementation(Libs.moquette_broker)
    implementation(project(":01-java-helloworld"))
    implementation(Libs.konf)
}

application {
    mainClassName = "demo.HelloProtelis"
}