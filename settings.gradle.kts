plugins {
    id("com.gradle.enterprise") version "3.17.3"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.5"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "protelis-demo"
include("01-java-helloworld")
include("02-kotlin-helloworld")
include("03-java-socket")
include("04-kotlin-socket")
include("05-java-mqtt")
include("06-kotlin-mqtt")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishOnFailure()
    }
}

gitHooks {
    commitMsg { conventionalCommits() }
    preCommit {
        tasks("ktlintCheck", "--parallel")
    }
    createHooks(overwriteExisting = true)
}
