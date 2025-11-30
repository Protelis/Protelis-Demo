# Copilot Instructions for Protelis-Demo

This repository contains demo applications showcasing the [Protelis](https://github.com/Protelis/Protelis) aggregate programming language using both Java and Kotlin implementations.

## Project Overview

Protelis-Demo is a multi-module Gradle project demonstrating various Protelis implementations:

- `01-java-helloworld`: Java implementation using an emulated network
- `02-kotlin-helloworld`: Kotlin implementation using an emulated network
- `03-java-socket`: Java implementation using sockets
- `04-kotlin-socket`: Kotlin implementation using sockets
- `05-java-mqtt`: Java implementation using MQTT protocol
- `06-kotlin-mqtt`: Kotlin implementation using MQTT protocol

## Build System

- **Build Tool**: Gradle with Kotlin DSL (`build.gradle.kts`)
- **Java Version**: Requires Java 17+
- **Kotlin Version**: See `gradle/libs.versions.toml` for the current version

### Common Commands

```bash
# Build all subprojects
./gradlew build

# Run a specific demo
./gradlew <subproject>:run
# Example: ./gradlew 01-java-helloworld:run

# Run tests
./gradlew test

# Run linting checks
./gradlew ktlintCheck
```

## Code Style Guidelines

### Java

- Follow standard Java conventions
- Use `final` for parameters and local variables where appropriate
- Use SpotBugs annotations for null analysis
- Private constructor for utility classes
- Javadoc comments for public methods and classes

### Kotlin

- Follow Kotlin coding conventions enforced by ktlint
- Use `object` for singleton classes
- Prefer immutable data structures
- Use `const val` for compile-time constants
- KDoc comments for public APIs

## Testing

### Java Tests

- Use JUnit 5 (`@Test`, `@BeforeAll`, `@DisplayName`)
- Use Mockito for mocking (`@ExtendWith(MockitoExtension.class)`)
- Package: `org.protelis.demo`

### Kotlin Tests

- Use Kotest with StringSpec style
- Use MockK for mocking (`spyk`, `verify`)
- Package: `org.protelis.demo`

## Project Structure

```
.
├── build.gradle.kts           # Root build configuration
├── settings.gradle.kts        # Project settings and subproject includes
├── gradle/libs.versions.toml  # Version catalog for dependencies
└── XX-language-demo/
    ├── build.gradle.kts       # Subproject-specific dependencies
    └── src/
        ├── main/
        │   ├── java|kotlin/   # Source code
        │   └── resources/     # Protelis programs (.pt files)
        └── test/
            └── java|kotlin/   # Test code
```

## Key Dependencies

- **Protelis**: Core aggregate programming library
- **JGraphT**: Graph library for network topology
- **Paho**: Eclipse MQTT client (for MQTT demos)
- **Moquette**: MQTT broker (for MQTT demos)

## Important Notes

- The `hello.pt` Protelis program is in the `resources` folder of each demo
- Network managers implement the `NetworkManager` interface
- Execution contexts extend `AbstractExecutionContext`
- The `Speaker` interface is used for output abstraction
