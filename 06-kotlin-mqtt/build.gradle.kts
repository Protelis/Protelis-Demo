repositories {
    jcenter()
}

dependencies {
    compile(project(":02-kotlin-helloworld"))
    compile(Libs.konf)
    implementation(Libs.org_eclipse_paho_client_mqttv3)
    implementation(Libs.moquette_broker)
}

application {
    mainClassName = "demo.HelloProtelisKt"
}
