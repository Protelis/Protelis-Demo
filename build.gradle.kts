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

    tasks.test {
        useJUnitPlatform()
    }
}

plugins {
    java
    application
    kotlin("jvm") version Versions.org_jetbrains_kotlin_jvm_gradle_plugin
    id("org.danilopianini.git-sensitive-semantic-versioning") version Versions.org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin
    id("com.github.spotbugs") version Versions.com_github_spotbugs_gradle_plugin
    checkstyle
    pmd
    id("org.jlleitschuh.gradle.ktlint") version Versions.org_jlleitschuh_gradle_ktlint_gradle_plugin
    id("de.fayard.buildSrcVersions") version Versions.de_fayard_buildsrcversions_gradle_plugin
}

gitSemVer {
    version = computeGitSemVer()
}

configure(subprojects.filter { it.name.contains("java") }) {
    apply(plugin = "java")
    // apply(plugin = "com.github.spotbugs")
    // apply(plugin = "checkstyle")
    // apply(plugin = "pmd")
    dependencies {
        testImplementation(Libs.junit_jupiter_api)
        testRuntime(Libs.junit_jupiter_engine)
        testCompile(Libs.mockito_core)
        testCompile(Libs.mockito_junit_jupiter)
    }
}

configure(subprojects.filter { it.name.contains("kotlin") }) {
    apply(plugin = "kotlin")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
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