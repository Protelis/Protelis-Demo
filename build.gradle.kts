import com.github.spotbugs.SpotBugsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

allprojects {
    apply(plugin = "application")
    apply(plugin = "org.danilopianini.git-sensitive-semantic-versioning")
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    repositories { mavenCentral() }
    dependencies {
        compile(Libs.protelis)
    }
    gitSemVer { version = computeGitSemVer() }
    tasks.test { useJUnitPlatform() }
    spotbugs {
        effort = "max"
        reportLevel = "low"
        isShowProgress = true
        val excludeFile = File("${rootProject.projectDir}/config/spotbugs/excludes.xml")
        if (excludeFile.exists()) {
            excludeFilter = excludeFile
        }
    }
    tasks.withType<SpotBugsTask> {
        reports {
            xml.setEnabled(false)
            html.setEnabled(true)
        }
    }
}
javaprojects {
    apply(plugin = "java")
    apply(plugin = "checkstyle")
    apply(plugin = "pmd")
    dependencies {
        testImplementation(Libs.junit_jupiter_api)
        testRuntime(Libs.junit_jupiter_engine)
        testCompile(Libs.mockito_core)
        testCompile(Libs.mockito_junit_jupiter)
    }

    checkstyle {
        toolVersion = "8.20"
    }

    pmd {
        ruleSets = listOf()
        ruleSetConfig = resources.text.fromFile("${rootProject.projectDir}/config/pmd/pmd.xml")
    }
}
kotlinprojects {
    apply(plugin = "kotlin")
    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        testImplementation(Libs.kotlintest_runner_junit5)
        testImplementation(Libs.mockk)
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            allWarningsAsErrors = true
            jvmTarget = "1.8"
        }
    }
}

inline fun kotlinprojects(todo: Project.() -> Any) = onProjectsWithLanguage("kotlin", todo)
inline fun javaprojects(todo: Project.() -> Any) = onProjectsWithLanguage("java", todo)
inline fun Project.onProjectsWithLanguage(language: String, todo: Project.() -> Any) = subprojects
    .filter { it.name.contains(language) }
    .forEach { with(it, todo) }
