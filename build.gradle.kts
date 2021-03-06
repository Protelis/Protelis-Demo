import com.github.spotbugs.snom.SpotBugsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm")
    id("org.danilopianini.git-sensitive-semantic-versioning")
    id("com.github.spotbugs")
    checkstyle
    pmd
    id("org.jlleitschuh.gradle.ktlint")
}

allprojects {
    apply(plugin = "application")
    apply(plugin = "org.danilopianini.git-sensitive-semantic-versioning")
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.protelis:protelis:_")
    }

    gitSemVer { version = computeGitSemVer() }
    tasks.test { useJUnitPlatform() }
    spotbugs {
        setEffort("max")
        setReportLevel("low")
        showProgress.set(true)
        val excludeFile = File("${project.rootProject.projectDir}/config/spotbugs/excludes.xml")
        if (excludeFile.exists()) {
            excludeFilter.set(excludeFile)
        }
    }
    tasks.withType<SpotBugsTask> {
        reports {
            create("html") { enabled = true }
        }
    }
}
javaprojects {
    apply(plugin = "java")
    apply(plugin = "checkstyle")
    apply(plugin = "pmd")
    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:_")
        testImplementation("org.mockito:mockito-core:_")
        testImplementation("org.mockito:mockito-junit-jupiter:_")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
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
        testImplementation("io.kotlintest:kotlintest-runner-junit5:_")
        testImplementation("io.mockk:mockk:_")
        // The following dependency forces mockk to use Kotlin 1.4.0 before official support
        // and can be removed once https://github.com/mockk/mockk/issues/483 and
        // https://github.com/mockk/mockk/issues/475 are fixed.
        testImplementation(kotlin("reflect"))
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
