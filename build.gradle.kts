import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

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
        implementation(rootProject.libs.protelis)
    }

    multiJvm {
        jvmVersionForCompilation.set(11)
    }

    tasks.withType<Test>().configureEach { useJUnitPlatform() }

    application {
        mainClass.set("org.protelis.demo.HelloProtelis")
    }

    tasks.withType<ShadowJar>().configureEach {
        val rootProjectShadow = rootProject.tasks.shadowJar.get()
        if (this != rootProjectShadow) {
            destinationDirectory.set(rootProjectShadow.destinationDirectory)
        } else {
            enabled = false
        }
    }
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

    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            allWarningsAsErrors = true
        }
    }
}

inline fun kotlinprojects(todo: Project.() -> Any) = onProjectsWithLanguage("kotlin", todo)

inline fun javaprojects(todo: Project.() -> Any) = onProjectsWithLanguage("java", todo)

inline fun Project.onProjectsWithLanguage(
    language: String,
    todo: Project.() -> Any,
) = subprojects
    .filter { it.name.contains(language) }
    .forEach { with(it, todo) }
