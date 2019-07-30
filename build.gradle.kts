import com.github.spotbugs.SpotBugsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "application")
    apply(plugin = "org.danilopianini.git-sensitive-semantic-versioning")

    dependencies {
        compile(Libs.protelis)
    }
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
    id("de.fayard.buildSrcVersions") version "0.3.2"
}

gitSemVer {
    version = computeGitSemVer()
}

configure(subprojects.filter { it.name.contains("java") }) {
    apply(plugin = "java")
    //apply(plugin = "com.github.spotbugs")
    //apply(plugin = "checkstyle")
    //apply(plugin = "pmd")
    dependencies {
        testImplementation(Libs.junit_jupiter_api)
        testRuntime(Libs.junit_jupiter_engine)
        testCompile(Libs.mockito_core)
        testCompile(Libs.mockito_junit_jupiter)
    }
}

configure(subprojects.filter { it.name.contains("kotlin") }) {
    apply(plugin = "kotlin")
    //apply(plugin = "org.jlleitschuh.gradle.ktlint")
    dependencies {
        implementation(Libs.kotlin_stdlib)
        testImplementation(Libs.kotlintest_runner_junit5)
        testImplementation(Libs.mockk)
    }
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