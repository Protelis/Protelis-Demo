plugins {
    id("com.gradle.enterprise") version "3.11.1"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.1"
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
        publishAlways()
    }
}

gitHooks {
    commitMsg { conventionalCommits() }
    preCommit {
        tasks("ktlintCheck", "checkScalafmt", "--parallel")
    }
    createHooks(overwriteExisting = true)
}
