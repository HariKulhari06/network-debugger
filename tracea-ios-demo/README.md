# Tracea iOS Native SwiftUI Demo

This directory contains the Swift/SwiftUI source code files for a native iOS application demonstrating how to integrate and use **Tracea** inside native iOS projects.

---

## 🛠️ Step 1: Compile the Tracea iOS Framework
Before opening the demo in Xcode, you must compile the shared Kotlin Multiplatform library into a native iOS `Tracea.xcframework` bundle.

On any Mac with Xcode installed, run the following Gradle task at the root of the `tracea` repository:
```bash
./gradlew :tracea:assembleTraceaReleaseXCFramework
```
This builds and bundles all simulator and device slices into a single framework at:
`tracea/build/XCFrameworks/release/Tracea.xcframework`

---

## 🛠️ Step 2: Set Up the Xcode Project
1. **Open Xcode** and select **File > New > Project...**
2. Choose **iOS > App** and click **Next**.
3. Configure the project:
   * **Product Name**: `TraceaDemo`
   * **Organization Identifier**: `com.hari.tracea`
   * **Interface**: `SwiftUI`
   * **Language**: `Swift`
4. Click **Next** and save the project in a directory of your choice.

---

## 🛠️ Step 3: Link the Tracea Framework
1. Select the `TraceaDemo` target in the project settings sidebar.
2. Navigate to the **General** tab.
3. Scroll down to the **Frameworks, Libraries, and Embedded Content** section.
4. Drag and drop `Tracea.xcframework` (from `tracea/build/XCFrameworks/release/`) into this list.
5. Ensure the **Embed** option for `Tracea.xcframework` is set to **Embed & Sign**.

---

## 🛠️ Step 4: Add the Demo Files
1. Drag the following Swift files from this directory into your Xcode project:
   * [`TraceaDemoApp.swift`](file:///Users/hari/Documents/kids/Learning/Android/tracea/tracea-ios-demo/TraceaDemo/TraceaDemoApp.swift) (Overwrite the default App entry file)
   * [`ContentView.swift`](file:///Users/hari/Documents/kids/Learning/Android/tracea/tracea-ios-demo/TraceaDemo/ContentView.swift) (Overwrite the default ContentView file)
   * [`NetworkManager.swift`](file:///Users/hari/Documents/kids/Learning/Android/tracea/tracea-ios-demo/TraceaDemo/NetworkManager.swift) (Add as a new file)
2. Build and run the project using **Cmd + R** on any iOS Simulator!

---

## 🔬 How Tracea Works on iOS
1. **Initialization**: On launch, `TraceaDemoApp.swift` initializes the library:
   ```swift
   Tracea.shared.initialize(context: nil, config: TraceaConfig(enabled: true))
   ```
2. **Database Path**: Because `context` is passed as `nil`, the iOS implementation of Room KMP automatically instantiates the sqlite database file at `NSHomeDirectory() + "/Documents/tracea_db"`.
3. **Manual capture API**: `NetworkManager.swift` tracks URLSession calls dynamically and reports them to Tracea:
   ```swift
   let traceaCall = Tracea.shared.startRequest(method: "GET", url: "https://...")
   // After request finishes:
   traceaCall.response(statusCode: 200, headers: [...], body: "...", contentType: "application/json")
   ```
4. **Log persistence**: All network calls (URLs, headers, request/response bodies, durations, errors) are immediately recorded in the local Room database and can be inspected or exported.
