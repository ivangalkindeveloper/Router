# Router for Swift

Router provides typed screen navigation, sheets, full-screen covers, route arguments, and a SwiftUI host.

[Platform comparison](../README.md).

## Installation and build

Requires Swift 6.1+. The navigation model supports iOS 16+ and macOS 13+; `RouterView` is explicitly unavailable on macOS. Build the SwiftUI host for iOS.

```swift
.package(url: "https://github.com/ivangalkindeveloper/Router.git", from: "2.0.0")
```

The repository root and `swift/` both contain a Swift Package Manager manifest. Open either in Xcode and select an iOS Simulator. From this directory:

```sh
xcodebuild -scheme Router \
  -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath .build/ios \
  CODE_SIGNING_ALLOWED=NO build
```

## Routes and operations

```swift
import Router

enum Screen: Hashable { case home, profile }
enum Sheet: Hashable { case filters }
enum FullScreen: Hashable { case login }

struct ProfileArguments: RouteArgumentsProtocol {
    let userID: String
}

let router = Router<Screen, Sheet, FullScreen>(
    initialRoute: RouteScreen(type: .home)
)
router.push(route: RouteScreen(type: .profile, arguments: ProfileArguments(userID: "user1")))
router.push(route: RouteSheet(type: .filters))
router.push(route: RouteFullScreenCover(type: .login))
router.dismissFullScreenCover()
router.dismissSheet()
router.pop()
router.popRoot()
router.root(route: RouteScreen(type: .home))
```

`root(route:)` changes the root screen and preserves the current path and modals. For a complete reset, also call `popRoot()` and the dismiss methods. Only call `pop()` when the path is non-empty.

Route equality and hashing use `type` and `id`; arguments are not part of equality. The route ID comes from `type.hashValue` and is not a persistent identifier.

## SwiftUI host

Implement `RouterBuilderProtocol`, with one `build(route:)` overload for each route kind, then provide the builder and initial route to `RouterView`:

```swift
import SwiftUI
import Router

struct Builder: RouterBuilderProtocol {
    func build(route: RouteScreen<Screen>) -> Text {
        switch route.type {
        case .home: Text("Home")
        case .profile:
            Text((route.arguments as? ProfileArguments)?.userID ?? "")
        }
    }

    func build(route: RouteSheet<Sheet>) -> Text {
        Text("Filters")
    }

    func build(route: RouteFullScreenCover<FullScreen>) -> Text {
        Text("Login")
    }
}

struct AppView: View {
    var body: some View {
        RouterView(
            builder: Builder(),
            initialRouteScreen: RouteScreen<Screen>(type: .home)
        )
    }
}
```

`RouterView` owns its router using `StateObject` and injects it into the environment. Child views can use `@EnvironmentObject var router: Router<Screen, Sheet, FullScreen>` to push routes or dismiss presentations. Update the SwiftUI navigation model on the main thread.
