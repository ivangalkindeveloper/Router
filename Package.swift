// swift-tools-version: 6.1
// The standalone Swift package is in swift/.
// This manifest preserves Swift Package Manager integration from the repository root.

import PackageDescription

let package = Package(
    name: "Router",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        .library(name: "Router", targets: ["Router"]),
    ],
    targets: [
        .target(
            name: "Router",
            path: "swift/Sources/Router"
        ),
        .testTarget(
            name: "RouterTests",
            dependencies: ["Router"],
            path: "swift/Tests/RouterTests"
        ),
    ]
)
