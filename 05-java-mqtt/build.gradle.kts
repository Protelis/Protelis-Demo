plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.org_eclipse_paho_client_mqttv3)
    implementation(Libs.moquette_broker)
    implementation(project(":01-java-helloworld"))
    implementation(Libs.konf)
}

application {
    mainClass.set("demo.HelloProtelis")
}
