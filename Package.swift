// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "Mimosa",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "Mimosa",
            targets: ["MimosaPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "MimosaPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/MimosaPlugin"),
        .testTarget(
            name: "MimosaPluginTests",
            dependencies: ["MimosaPlugin"],
            path: "ios/Tests/MimosaPluginTests")
    ]
)