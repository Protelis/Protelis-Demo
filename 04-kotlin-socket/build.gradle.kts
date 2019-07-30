dependencies {
    compile(project(":02-kotlin-helloworld"))
    compile("com.uchuhimo:konf:0.13.3")
}

application {
    mainClassName = "demo.HelloProtelisKt"
}
