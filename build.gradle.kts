import com.github.spotbugs.snom.SpotBugsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION as KOTLIN_VERSION

plugins {
    java
    application
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.java.qa)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.shadowJar)
}

allprojects {
    apply(plugin = "application")
    with(rootProject.libs.plugins) {
        apply(plugin = gitSemVer.get().pluginId)
        apply(plugin = java.qa.get().pluginId)
        apply(plugin = multiJvmTesting.get().pluginId)
        apply(plugin = kotlin.qa.get().pluginId)
        apply(plugin = shadowJar.get().pluginId)
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.protelis:protelis:15.1.0")
    }

    tasks.withType<Test>().configureEach { useJUnitPlatform() }
}
javaprojects {
    dependencies {
        with(rootProject.libs) {
            compileOnly(spotbugs.annotations)
            testImplementation(junit.api)
            testImplementation(bundles.mockito)
            testRuntimeOnly(junit.engine)
        }
    }
}
kotlinprojects {
    apply(plugin = "kotlin")
    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        with(rootProject.libs) {
            testImplementation(kotest.assertions.core)
            testImplementation(mockk)
            testImplementation(kotest.runner)
        }
    }

    // Enforce Kotlin version coherence
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin")) {
                useVersion(KOTLIN_VERSION)
                because("All Kotlin modules should use the same version, and compiler uses $KOTLIN_VERSION")
            }
        }
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
