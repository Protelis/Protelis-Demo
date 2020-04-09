import kotlin.String

/**
 * Find which updates are available by running
 *     `$ ./gradlew buildSrcVersions`
 * This will only update the comments.
 *
 * YOU are responsible for updating manually the dependency version. */
object Versions {
    const val com_github_spotbugs_gradle_plugin: String = "2.0.0" // available: "2.0.1"

    const val ktlint: String = "0.33.0" // available: "0.35.0"

    const val konf: String = "0.13.3" // available: "0.20.0"

    const val de_fayard_buildsrcversions_gradle_plugin: String = "0.3.2" // available: "0.7.0"

    const val kotlintest_runner_junit5: String = "3.3.2" // available: "3.4.2"

    const val mockk: String = "1.9.1" // available: "1.9.3"

    const val moquette_broker: String = "0.12.1" 

    const val org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin: String = "0.2.2" 

    const val org_eclipse_paho_client_mqttv3: String = "1.2.2" 

    const val org_jetbrains_kotlin_jvm_gradle_plugin: String = "1.3.40" // available: "1.3.50"

    const val org_jetbrains_kotlin: String = "1.3.40" // available: "1.3.50"

    const val jgrapht_core: String = "1.3.1" 

    const val org_jlleitschuh_gradle_ktlint_gradle_plugin: String = "8.2.0" // available: "9.1.0"

    const val org_junit_jupiter: String = "5.4.2" // available: "5.5.2"

    const val mockito_core: String = "2.1.0" // available: "3.1.0"

    const val mockito_junit_jupiter: String = "2.23.0" // available: "3.1.0"

    const val protelis: String = "13.2.0" // available: "13.1.0"

    const val slf4j_simple: String = "1.8.0-beta4" 

    /**
     *
     *   To update Gradle, edit the wrapper file at path:
     *      ./gradle/wrapper/gradle-wrapper.properties
     */
    object Gradle {
        const val runningVersion: String = "5.4.1"

        const val currentVersion: String = "5.6.4"

        const val nightlyVersion: String = "6.1-20191107230047+0000"

        const val releaseCandidate: String = "6.0-rc-3"
    }
}
