# Router — platform differences

The Swift implementation binds navigation to SwiftUI. The Kotlin implementation separates a JVM navigation model from an Android Jetpack Compose host.

| Aspect | Swift / SwiftUI | Kotlin / Android |
|---|---|---|
| Implementation and usage | [swift/README.md](swift/README.md) | [kotlin/README.md](kotlin/README.md) |
| Package / namespace | `Router` | `io.ivangalkindeveloper.router` and `.router.compose` |
| Platforms | Model: iOS 16+, macOS 13+; UI host: iOS | JVM model; Compose host: Android API 23+ |
| Build requirements | Swift 6.1+, Swift Package Manager / Xcode | JDK 17, Gradle; Android SDK for the Compose module |
| Navigation state | `NavigationPath`, `ObservableObject`, `@Published` | Immutable `RouterState` snapshots exposed through `StateFlow` |
| UI host | SwiftUI `NavigationStack` and `RouterView` | Jetpack Compose `RouterView` |
| Sheet | SwiftUI `.sheet` | Material 3 `ModalBottomSheet` |
| Full-screen presentation | SwiftUI `.fullScreenCover` | Full-screen Compose `Dialog` |
| Access from child views | `environmentObject` | `LocalRouter` / `currentRouter` |
| Model ownership | Host-owned `StateObject` | Caller-owned model, typically in a `ViewModel`; an optional remembered model is also available |
| Back navigation | Native SwiftUI navigation behavior | Android Back closes full-screen presentation, sheet, then the top screen |
| Empty-stack `pop` | Caller must check that the path is non-empty | Safely does nothing |
| Screen state | Managed by SwiftUI | Unique entry keys and retained `rememberSaveable` state; ordinary `remember` state is recreated on return |
| Transitions | Native SwiftUI transitions | Standard modal animations; screen transition animations and predictive back are not implemented |

The current Swift `RouterView` is explicitly unavailable on macOS even though the navigation model supports macOS. The Android host requires Compose; the Kotlin model can be used without it.

Keeping the Kotlin model in a `ViewModel` preserves it across configuration changes. Restoring arbitrary route types and arguments after process death requires application-defined serialization. See each implementation's README for its own navigation API and usage.
