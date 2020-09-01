import de.fayard.refreshVersions.bootstrapRefreshVersions
import org.danilopianini.VersionAliases.justAdditionalAliases
buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("de.fayard.refreshVersions:refreshVersions:0.9.5")
        classpath("org.danilopianini:refreshversions-aliases:+")
    }
}
bootstrapRefreshVersions(justAdditionalAliases)

rootProject.name = "protelis-demo"
include("01-java-helloworld")
include("02-kotlin-helloworld")
include("03-java-socket")
include("04-kotlin-socket")
include("05-java-mqtt")
include("06-kotlin-mqtt")