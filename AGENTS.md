# AGENTS.md

Guidance for coding agents working on this repository.

## What this is

Demo applications for [Protelis](https://github.com/Protelis/Protelis), an aggregate programming language on the JVM.
It is a Gradle multi-project build. Each demo has a Java version and a matching Kotlin version:

| Subproject             | Language | Network layer                      | Depends on              |
|------------------------|----------|------------------------------------|-------------------------|
| `01-java-helloworld`   | Java     | in-memory `EmulatedNetworkManager` | —                       |
| `02-kotlin-helloworld` | Kotlin   | in-memory `EmulatedNetworkManager` | —                       |
| `03-java-socket`       | Java     | sockets, config via konf           | `:01-java-helloworld`   |
| `04-kotlin-socket`     | Kotlin   | sockets, config via konf           | `:02-kotlin-helloworld` |
| `05-java-mqtt`         | Java     | MQTT (Paho + Moquette broker)      | `:01-java-helloworld`   |
| `06-kotlin-mqtt`       | Kotlin   | MQTT (Paho + Moquette broker)      | `:02-kotlin-helloworld` |

All sources live in the package `org.protelis.demo`. Protelis programs are `.pt` files under `src/main/resources`.
The entry point of every subproject is `org.protelis.demo.HelloProtelis`.

When you change one demo, make the same change to its counterpart in the other language, so the two stay equivalent.

## Build layout

- `build.gradle.kts` (root) configures **all** subprojects through `allprojects { }`, `javaprojects { }` and `kotlinprojects { }`.
  A subproject counts as Java or Kotlin based on whether its **name** contains `java` or `kotlin`.
  Subproject `build.gradle.kts` files only declare extra dependencies.
- Dependency and plugin versions are only in `gradle/libs.versions.toml`. Do not hard-code versions in build scripts.
- Compilation targets the JVM set by `multiJvm { jvmVersionForCompilation }`.
  The multi-jvm-test plugin also runs the tests on other LTS JVMs (`testWithJvmNN` tasks).
  Toolchains are downloaded automatically through the foojay resolver.
- Shadow jars are written to the root project's output directory; the root project's own `shadowJar` is disabled.

## Commands

```bash
./gradlew build                  # compile, test on all JVMs, run every QA check
./gradlew build -x test          # quick check without tests
./gradlew ktlintCheck            # what the pre-commit hook runs
./gradlew ktlintFormat           # auto-fix Kotlin formatting
./gradlew :04-kotlin-socket:run  # run one demo
```

On a busy machine, a full parallel build can run the Gradle daemon out of Metaspace.
If that happens, retry with `-Dorg.gradle.jvmargs="-Xmx3g -XX:MaxMetaspaceSize=2g"`.

## Quality gates (CI fails on any of these)

- **Kotlin**: `allWarningsAsErrors = true`, plus detekt and ktlint from `gradle-kotlin-qa`.
  New plugin versions often add detekt rules. For example, an unused `private val` constructor property fails `UnusedPrivateProperty`; make it a plain constructor parameter instead.
- **Java**: Checkstyle, PMD and SpotBugs from `gradle-java-qa`.
  SpotBugs annotations are available as `compileOnly`.
- Every public class and member needs KDoc or Javadoc.
- Run `./gradlew build` locally before pushing.
  CI builds on Ubuntu, macOS and Windows with `DanySK/build-check-deploy-gradle-action`.

## Commits and releases

- Gradle installs git hooks when it configures the build (`settings.gradle.kts`):
  - `commit-msg` requires [Conventional Commits](https://www.conventionalcommits.org/), including merge commits.
    Allowed types are `fix feat build chore ci docs perf refactor revert style test`.
  - `pre-commit` runs `ktlintCheck`.
- Hooks are not generated in a detached git worktree. Commits made there skip the hooks, so check them by hand.
- Releases come from `semantic-release` (`release.config.mjs`, `package.json`) and are driven by commit types.
  Do not edit `CHANGELOG.md` or version numbers by hand; `gitSemVer` derives the version from git tags.

## Dependency updates

Renovate opens most PRs (`renovate.json`) with auto-merge on. Once CI passes, they go through the Mergify merge queue (`.mergify.yml` extends the shared `mergify-config`).
To fix a failing Renovate PR, push a follow-up commit to its branch.
If the branch is behind `master` and has conflicts, merge `master` into it rather than force-pushing.
Major Protelis upgrades may raise the minimum JVM. When they do, update `jvmVersionForCompilation` in the root build.
