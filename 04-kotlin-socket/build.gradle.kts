dependencies {
    compile(project(":02-kotlin-helloworld"))
    compile(Libs.konf)
}

application {
    mainClassName = "demo.HelloProtelisKt"
}
