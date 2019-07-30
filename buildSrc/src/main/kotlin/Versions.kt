import kotlin.String

/**
 * Find which updates are available by running
 *     `$ ./gradlew buildSrcVersions`
 * This will only update the comments.
 *
 * YOU are responsible for updating manually the dependency version. */
object Versions {
    const val com_github_spotbugs_gradle_plugin: String = "2.0.0" 

    const val ktlint: String = "0.33.0" // available: "0.34.2"

    const val konf: String = "0.13.3" 

    const val de_fayard_buildsrcversions_gradle_plugin: String = "0.3.2" 

    const val kotlintest_runner_junit5: String = "3.3.2" // available: "3.4.0"

    const val mockk: String = "1.9.1" // available: "1.9.3"

    const val org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin: String = "0.2.2" 

    const val org_jetbrains_kotlin_jvm_gradle_plugin: String = "1.3.40" // available: "1.3.41"

    const val org_jetbrains_kotlin: String = "1.3.40" // available: "1.3.41"

    const val jgrapht_core: String = "1.3.1" 

    const val org_jlleitschuh_gradle_ktlint_gradle_plugin: String = "8.2.0" 

    const val org_junit_jupiter: String = "5.4.2" // available: "5.5.1"

    const val mockito_core: String = "2.1.0" // available: "3.0.0"

    const val mockito_junit_jupiter: String = "2.23.0" // available: "3.0.0"

    const val protelis: String = "12.1.0" // available: "12.2.0"

    const val slf4j_simple: String = "1.8.0-beta4" 

    /**
     *
     *   To update Gradle, edit the wrapper file at path:
     *      ./gradle/wrapper/gradle-wrapper.properties
     */
    object Gradle {
        const val runningVersion: String = "5.4.1"

        const val currentVersion: String = "5.5.1"

        const val nightlyVersion: String = "5.7-20190729220032+0000"

        const val releaseCandidate: String = "5.6-rc-1"
    }
}
