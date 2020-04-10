repositories {
    jcenter()
}

dependencies {
    implementation(project(":02-kotlin-helloworld"))
    implementation(Libs.konf)
    implementation(Libs.org_eclipse_paho_client_mqttv3)
    implementation(Libs.moquette_broker)
}

application {
    mainClassName = "demo.HelloProtelisKt"
}
