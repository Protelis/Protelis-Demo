// moquette needs jcenter
repositories {
    jcenter()
}

dependencies {
    implementation(Libs.kotlin_stdlib)
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.2")
    implementation("io.moquette:moquette-broker:0.12.1")
    implementation(project(":01-java-helloworld"))
    implementation(Libs.konf)
}

application {
    mainClassName = "demo.HelloProtelis"
}