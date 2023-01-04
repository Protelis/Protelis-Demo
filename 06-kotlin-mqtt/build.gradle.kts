dependencies {
    implementation(project(":02-kotlin-helloworld"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.konf)
    implementation(libs.paho)
    implementation(libs.moquette.broker)
}

application {
    mainClassName = "demo.HelloProtelisKt"
}
