# Tracea iOS Native SwiftUI Demo

This directory contains a pre-configured native Xcode project (**`TraceaDemo.xcodeproj`**) to demonstrate how to integrate and use **Tracea** inside native iOS projects.

With this project, you do not need to manually configure project build settings or link target dependencies—everything is pre-wired to find the compiled KMP framework.

---

## 🛠️ Step 1: Compile the Tracea iOS Framework
Before opening the demo in Xcode, you must compile the shared Kotlin Multiplatform library into a native iOS `Tracea.xcframework` bundle.

On any Mac with Xcode installed, run the following Gradle task at the root of the `tracea` repository:
```bash
./gradlew :tracea:assembleTraceaReleaseXCFramework
```
This builds and bundles the framework at:
`tracea/build/XCFrameworks/release/Tracea.xcframework`

---

## 🛠️ Step 2: Open and Run in Xcode
1. **Open the project**: Double-click **`TraceaDemo.xcodeproj`** to open it directly in Xcode.
2. **Device Selection**: Choose any iOS Simulator (e.g. iPhone 15) from the target device dropdown at the top.
3. **Build and Run**: Press **Cmd + R** or click the **Play** button to build and run the application.

---

## 🔬 How Tracea is Configured
* **Relative Framework Linking**: The Xcode project is pre-configured to search for the framework in the relative directory:
  `$(PROJECT_DIR)/../tracea/build/XCFrameworks/release`
  and automatically embeds and signs the binary.
* **App Launch Initialization**: On startup (`TraceaDemoApp.swift`), Tracea is initialized:
  ```swift
  Tracea.shared.initialize(context: nil, config: TraceaConfig(enabled: true))
  ```
  Since `context` is passed as `nil`, Room KMP automatically creates the log database at `NSHomeDirectory() + "/Documents/tracea_db"`.
* **Automatic Manual Capture Logging**: `NetworkManager.swift` performs HTTP request calls via URLSession and records metadata, timing, and response payloads directly to Tracea:
  ```swift
  let traceaCall = Tracea.shared.startRequest(method: "GET", url: "https://...")
  // After request completes:
  traceaCall.response(statusCode: 200, headers: [...], body: "...", contentType: "application/json")
  ```
