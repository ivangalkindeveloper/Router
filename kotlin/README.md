# Router for Kotlin / Android

Typed screen navigation, route arguments, sheets, full-screen presentations, and a Jetpack Compose host.

[Platform comparison](../README.md). Open this `kotlin/` directory in Android Studio and run commands from here.

## Installation

Modules:

- `router`: JVM navigation model, route types, `RouterProtocol`, and state exposed as `StateFlow`.
- `router-compose`: Android AAR containing `RouterView`, `RouterBuilderProtocol`, `LocalRouter`, `currentRouter`, and `rememberRouter`.
- `sample`: Android app demonstrating profiles, a filter sheet, and full-screen login.

Requirements: JDK 17, Android SDK 36, Build Tools 35.0.0, and Android API 23+. The project uses Gradle 8.13, AGP 8.11.1, Kotlin 2.2.21, and Compose BOM 2025.09.01. Set the SDK through `ANDROID_HOME` or an ignored `local.properties` file containing `sdk.dir=/path/to/Android/sdk`.

```sh
./gradlew test assembleDebug lintDebug publishToMavenLocal
```

Add `mavenLocal()` to your application's repositories, alongside `google()` and `mavenCentral()`:

```kotlin
dependencies {
    implementation("io.ivangalkindeveloper:router-compose:2.0.0")
    // The router model is included transitively.
}
```

Enable `buildFeatures { compose = true }` and the Kotlin Compose compiler plugin matching your Kotlin version. To use the model without Compose, depend on `io.ivangalkindeveloper:router:2.0.0` instead.

Source integration is available through `includeBuild("/absolute/path/to/Router/kotlin")` in the application's `settings.gradle.kts`, using the same coordinates. Remote Maven publishing requires separate repository and credential configuration.

## Routes

```kotlin
import io.ivangalkindeveloper.router.*

enum class Screen { HOME, PROFILE }
enum class Sheet { FILTERS }
enum class FullScreen { LOGIN }
data class ProfileArguments(val userId: String) : RouteArgumentsProtocol

val router = Router<Screen, Sheet, FullScreen>(RouteScreen(Screen.HOME))
router.push(RouteScreen(Screen.PROFILE, ProfileArguments("user1")))
router.push(RouteSheet(Sheet.FILTERS))
router.push(RouteFullScreenCover(FullScreen.LOGIN))
router.dismissFullScreenCover()
router.dismissSheet()
router.pop()
router.popRoot()
router.root(RouteScreen(Screen.HOME))
```

Use enums or immutable data classes with stable `equals` and `hashCode` for route types. Arguments implement the marker interface `RouteArgumentsProtocol`.

`root(route)` replaces the root while retaining the path and presentations. Call `popRoot()` and the dismiss methods for a full reset. Calling `pop()` on an empty stack safely does nothing.

## Compose host

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import io.ivangalkindeveloper.router.*
import io.ivangalkindeveloper.router.compose.*

class Builder : RouterBuilderProtocol<Screen, Sheet, FullScreen> {
    @Composable override fun build(route: RouteScreen<Screen>) {
        val router = currentRouter<Screen, Sheet, FullScreen>()
        when (route.type) {
            Screen.HOME -> Button(onClick = {
                router.push(RouteScreen(Screen.PROFILE, ProfileArguments("user1")))
            }) { Text("Open profile") }
            Screen.PROFILE -> Text((route.arguments as? ProfileArguments)?.userId.orEmpty())
        }
    }
    @Composable override fun build(route: RouteSheet<Sheet>) {
        Text("Filters")
    }
    @Composable override fun build(route: RouteFullScreenCover<FullScreen>) {
        Text("Login")
    }
}

@Composable fun App(router: Router<Screen, Sheet, FullScreen>) {
    MaterialTheme { RouterView(router = router, builder = Builder()) }
}
```

Keep the router in a `ViewModel` to retain navigation across Activity recreation; see `sample`. `RouterView(builder = Builder(), initialRouteScreen = RouteScreen(Screen.HOME))` creates a remembered router for simpler lifetimes.

The type arguments of `currentRouter` must match the nearest host. Nested hosts supply their own router. `LocalRouter.current` exposes the interface without specifying route type arguments.

## State and presentation behavior

Route equality includes presentation kind, type, and ID, but not arguments. `id == type.hashCode()` is not a persistent identifier. Each push has a separate `RouteEntry.key`, including repeated identical routes, so each screen retains independent `rememberSaveable` state. Reopening a modal updates its arguments and state.

Back closes the full-screen presentation first, then the sheet, then the top screen. At the root it is passed to the Activity. Modal animations are provided by Compose. Screen transition animations and predictive back are not implemented.

`RouterView` retains previous screens' `rememberSaveable` state; ordinary `remember` state is recreated on return. State updates are atomic, and previously exposed snapshots and collections are not mutated. Use immutable route types and arguments.

A `ViewModel` handles configuration changes. Restoring navigation after process death requires your own serialization and reconstruction, for example with `SavedStateHandle`. A remembered router lives only as long as its composition.

## Example and checks

Run the `sample` module from Android Studio, or build its APK:

```sh
./gradlew :sample:assembleDebug
```

The APK is in `sample/build/outputs/apk/debug/`. JVM tests cover stack operations, arguments, equality, duplicate entries, snapshots, and concurrent pushes. Robolectric Compose tests cover retained screen state, repeated routes, and both presentation types.
