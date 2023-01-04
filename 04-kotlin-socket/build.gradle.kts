dependencies {
    implementation(project(":02-kotlin-helloworld"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.konf)
}

application {
    mainClassName = "demo.HelloProtelisKt"
}
