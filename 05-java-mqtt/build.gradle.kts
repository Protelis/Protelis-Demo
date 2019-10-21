// moquette needs jcenter
repositories {
    jcenter()
}

dependencies {
    implementation(Libs.kotlin_stdlib)
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.2")
    implementation(project(":01-java-helloworld"))
    implementation(Libs.konf)
//    testImplementation("hu.blackbelt.bundles.moquette:io.moquette:0.11_1")
    implementation("io.moquette:moquette-broker:0.12.1")
}

application {
    mainClassName = "demo.HelloProtelis"
}