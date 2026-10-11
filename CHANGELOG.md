# Changelog

## 2.0.0

This major release combines the SwiftUI and Kotlin / Android implementations in the existing Router repository. Both implementations share release version `2.0.0`, while retaining platform-specific navigation state and UI APIs.

### Changes

- Move the Swift package, tests, and usage guide into `swift/`.
- Add the Kotlin navigation model in `kotlin/router` and the Jetpack Compose host in `kotlin/router-compose`.
- Add an Android sample demonstrating screens, route arguments, sheets, and full-screen presentation.
- Use the `io.ivangalkindeveloper.router` and `io.ivangalkindeveloper.router.compose` namespaces.
- Retain the root `Package.swift` for Swift Package Manager integration using the existing repository URL.
- Align the Swift release tag, both Kotlin library versions, and Android sample version at `2.0.0`.
- Provide English platform usage guides and a root README focused on platform differences.
- Add GitHub Actions checks for Kotlin / Android and Swift, including an iOS Simulator build.
- Configure Android SDK setup to install `platform-tools` without requesting the removed `tools` package.

### Migration

- Swift: use `.package(url: "https://github.com/ivangalkindeveloper/Router.git", from: "2.0.0")`. Direct source and local package paths now use `swift/`.
- Android: open or include `kotlin/` and use `io.ivangalkindeveloper:router-compose:2.0.0` after publishing to Maven Local. The navigation model is included transitively.
- JVM: use `io.ivangalkindeveloper:router:2.0.0` when a Compose host is unnecessary.
- Consult [Swift usage](swift/README.md) and [Kotlin usage](kotlin/README.md) for routes, arguments, and presentation behavior.

### Validation

- Swift package build and tests passed.
- SwiftUI package build for iOS Simulator passed.
- Kotlin model and Compose tests passed.
- Android debug builds, lint, and Maven Local publication passed.

This is a GitHub source release; Maven Central publication is a separate distribution step. The SwiftUI host supports iOS. Android screen transitions, predictive back, and application-specific navigation restoration after process death are documented in the [Kotlin usage guide](kotlin/README.md).
