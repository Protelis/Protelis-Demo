dependencies {
    implementation(project(":02-kotlin-helloworld"))
    implementation(Libs.konf)
}

application {
    mainClassName = "demo.HelloProtelisKt"
}
