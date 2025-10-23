plugins {
    id("com.gradle.develocity") version "4.2.2"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.1.3"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "protelis-demo"
include("01-java-helloworld")
include("02-kotlin-helloworld")
include("03-java-socket")
include("04-kotlin-socket")
include("05-java-mqtt")
include("06-kotlin-mqtt")

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        uploadInBackground = !System.getenv("CI").toBoolean()
    }
}

gitHooks {
    commitMsg { conventionalCommits() }
    preCommit {
        tasks("ktlintCheck", "--parallel")
    }
    createHooks(overwriteExisting = true)
}
