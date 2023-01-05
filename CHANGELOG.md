## [11.0.1](https://github.com/Protelis/Protelis-Demo/compare/11.0.0...11.0.1) (2023-01-05)


### Dependency updates

* **core-deps:** update dependency org.jetbrains.kotlin.jvm to v1.8.0 ([8c94b37](https://github.com/Protelis/Protelis-Demo/commit/8c94b37afef5547ef849a9989a5408c7f14f448c))
* **deps:** update plugin gitsemver to v0.3.25 ([c130ade](https://github.com/Protelis/Protelis-Demo/commit/c130ade87c8646e7530c79875ccd7a28903fc51b))

## [11.0.0](https://github.com/Protelis/Protelis-Demo/compare/10.1.1...11.0.0) (2023-01-04)


### ⚠ BREAKING CHANGES

* trigger a major release

### Features

* trigger a major release ([df2a135](https://github.com/Protelis/Protelis-Demo/commit/df2a135c53e2aadadb40fdf7862d7c389b524b0c))


### Refactoring

* remove MyNetworkManager, add Junit tests ([1e4082b](https://github.com/Protelis/Protelis-Demo/commit/1e4082bb93d82e7dd6bcf1ded2864d9b16a612fe))


### Dependency updates

* **deps:** update dependency com.github.spotbugs:spotbugs-annotations to v4.7.3 ([40515f0](https://github.com/Protelis/Protelis-Demo/commit/40515f0d94bda688825f1ca0b738a1273fd1beef))
* **deps:** update dependency de.fraunhofer.iosb.io.moquette:moquette-broker to v0.15.1 ([090d7d7](https://github.com/Protelis/Protelis-Demo/commit/090d7d75569081e8e01a6ce3e4fc7cdd1634e02b))
* **deps:** update dependency gradle to v7.6 ([87548c4](https://github.com/Protelis/Protelis-Demo/commit/87548c4c2445551d797faac598b36843f8c6e33b))
* **deps:** update dependency io.mockk:mockk to v1.13.3 ([13d09d5](https://github.com/Protelis/Protelis-Demo/commit/13d09d51e6d868b67d77063e3b0b02cfe80a5c11))
* **deps:** update dependency org.protelis:protelis to v15.1.2 ([957d214](https://github.com/Protelis/Protelis-Demo/commit/957d214c6a5398de238710650c801315cafae9e0))
* **deps:** update dependency org.protelis:protelis to v15.4.2 ([249be6f](https://github.com/Protelis/Protelis-Demo/commit/249be6f27bb4fbb923f71a5c0aa85bae2318b3fa))
* **deps:** update dependency org.protelis:protelis to v16 ([6fede62](https://github.com/Protelis/Protelis-Demo/commit/6fede629231b88e91d8a2b99cad973b7e313afb6))
* **deps:** update junit5 monorepo to v5.9.1 ([75da547](https://github.com/Protelis/Protelis-Demo/commit/75da5476c99757c04bde98f9023f532e67e12b1c))
* **deps:** update mockito monorepo to v3.12.4 ([abe7978](https://github.com/Protelis/Protelis-Demo/commit/abe79789d4d6c8b79f9952f7d4341685889ac2d9))
* **deps:** update mockito monorepo to v4 ([a8b752b](https://github.com/Protelis/Protelis-Demo/commit/a8b752b048f7950a5d6bd928987568c8e4e9d533))


### Build and continuous integration

* configure shadowJar ([c5cfff8](https://github.com/Protelis/Protelis-Demo/commit/c5cfff8f8eb821cb2985826095230dffe0c8a594))
* **deps:** update actions/checkout action to v3 ([1348be7](https://github.com/Protelis/Protelis-Demo/commit/1348be70c9b974c749b080621d34a910f631c75a))
* **deps:** update actions/download-artifact action to v3 ([b1f1408](https://github.com/Protelis/Protelis-Demo/commit/b1f14084692b7f0c7ea2f3d57e2bed2697d24a60))
* **deps:** update al-cheb/configure-pagefile-action action to v1.3 ([32d240e](https://github.com/Protelis/Protelis-Demo/commit/32d240ec4ea2dcf5636c0c90baf453a6f9f3daad))
* **deps:** update joschi/setup-jdk action to v2.5.1 ([c06480a](https://github.com/Protelis/Protelis-Demo/commit/c06480a02e28a952c7158bcfc85486e5d1b969d5))
* do not cancel dispatches in progress, as they stop the release as well ([0d40d68](https://github.com/Protelis/Protelis-Demo/commit/0d40d68f4b05b8163c29a1579dc9a17ef2d7b0e7))
* do not double-generate the fat jars ([64f055e](https://github.com/Protelis/Protelis-Demo/commit/64f055ec79fe50d2d280d529b139c39d83d8361c))
* drop the custom static analysis configuration and use the QA plugins defaults ([6c7dd75](https://github.com/Protelis/Protelis-Demo/commit/6c7dd75b978f6c5f4c9dbec1129d27c54126b1f5))
* enable mergify ([83d41eb](https://github.com/Protelis/Protelis-Demo/commit/83d41eb9a44519b968b2152a247f525c8b68e8a7))
* move protelis to the catalog ([d9f1744](https://github.com/Protelis/Protelis-Demo/commit/d9f1744ae38387ae28c2c77106b88457cd999719))
* rebuild the CI ([c89fb6a](https://github.com/Protelis/Protelis-Demo/commit/c89fb6af8404a5e23a454ae04daa754b2c2e4650))
* refresh the build system ([cbd92f0](https://github.com/Protelis/Protelis-Demo/commit/cbd92f06a0150c31f4c7a92df57fddb9ed1ab090))
* renovate the infrastructure and fix multiple style issues ([8daceb5](https://github.com/Protelis/Protelis-Demo/commit/8daceb58c169e5a2f5cad2bb9137eadd44cadc01))
* **renovate:** switch to a preset meant for kotlin projects ([58bc039](https://github.com/Protelis/Protelis-Demo/commit/58bc039fcc7cb45f909c173f39d103ccc1a60b3c))
* try to limit the CI backlog by canceling same-PR dispatches ([428e2f1](https://github.com/Protelis/Protelis-Demo/commit/428e2f1c412556a860995e7a43870d595f275162))
