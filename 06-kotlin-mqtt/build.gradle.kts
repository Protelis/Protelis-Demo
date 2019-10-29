repositories {
    jcenter()
}

dependencies {
    compile(project(":02-kotlin-helloworld"))
    compile(Libs.konf)
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.2")
    implementation("io.moquette:moquette-broker:0.12.1")
}

application {
    mainClassName = "demo.HelloProtelisKt"
}
