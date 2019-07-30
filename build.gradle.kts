import com.github.spotbugs.SpotBugsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "application")
    apply(plugin = "org.danilopianini.git-sensitive-semantic-versioning")

    dependencies {
        compile("org.protelis:protelis:12.1.0")
    }

//    tasks.test {
//    }
}

plugins {
    java
    application
    kotlin("jvm") version "1.3.40"
    id("org.danilopianini.git-sensitive-semantic-versioning") version "0.2.2"
    id("com.github.spotbugs") version "2.0.0"
    checkstyle
    pmd
    id("org.jlleitschuh.gradle.ktlint") version "8.2.0"
}

gitSemVer {
    version = computeGitSemVer()
}

configure(subprojects.filter { it.name.contains("java") }) {
    apply(plugin = "java")
    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
        testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
        testCompile("org.mockito:mockito-core:2.1.0")
        testCompile("org.mockito:mockito-junit-jupiter:2.23.0")
    }
    //apply(plugin = "com.github.spotbugs")
    //apply(plugin = "checkstyle")
    //apply(plugin = "pmd")
}

configure(subprojects.filter { it.name.contains("kotlin") }) {
    apply(plugin = "kotlin")
    dependencies {
        implementation(kotlin("stdlib"))
        testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
        testImplementation("io.mockk:mockk:1.9.1")
    }
    //apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

spotbugs {
    effort = "max"
    reportLevel = "low"
    isShowProgress = true
    val excludeFile = File("${project.rootProject.projectDir}/config/spotbugs/excludes.xml")
    if (excludeFile.exists()) {
        excludeFilter = excludeFile
    }
}

pmd {
    ruleSets = listOf()
    ruleSetConfig = resources.text.fromFile("${project.rootProject.projectDir}/config/pmd/pmd.xml")
}

tasks.withType<SpotBugsTask> {
    reports {
        xml.setEnabled(false)
        html.setEnabled(true)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
    }
}

tasks.test {
    useJUnitPlatform()
}