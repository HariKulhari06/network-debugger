# 📡 Tracea

<p align="center">
  <img src="logo.png" width="160" alt="Tracea Logo"/>
</p>

<p align="center">
  <a href="https://jitpack.io/#HariKulhari06/tracea"><img src="https://jitpack.io/v/HariKulhari06/tracea.svg" alt="JitPack"/></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License"/></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.1.20-purple.svg" alt="Kotlin"/></a>
  <a href="https://kotlinlang.org/docs/multiplatform.html"><img src="https://img.shields.io/badge/Platform-Kotlin_Multiplatform-blue.svg" alt="Platform"/></a>
  <img src="https://img.shields.io/badge/Targets-Android_|_iOS-orange.svg" alt="Targets"/>
</p>

**Tracea** is a premium, high-performance in-app network debugger built with **Kotlin Multiplatform (KMP)**. It allows developers to capture, persist, redact, and mock network traffic natively on both **Android** and **iOS** with zero external proxy dependencies.

---

## 📱 Visual Overview

| Android Network Inspector | Android Mocking Panel | iOS SwiftUI Demo App |
| :---: | :---: | :---: |
| <img src="screenshot/Screenshot_20260813_093825.png" width="280" alt="Network List"/> | <img src="screenshot/Screenshot_20260813_093900.png" width="280" alt="Mock Rules"/> | <img src="tracea-ios-demo/screenshot_ios.png" width="280" alt="iOS Demo Interface" onError="this.style.display='none'"/> |

---

## ✨ Features

- 🔍 **Real-time Traffic Inspection** — Monitor all incoming and outgoing HTTP/HTTPS calls with color-coded status badges and request/response metrics.
- 🎭 **Dynamic API Mocking** — Intercept request sessions, matching endpoints to custom JSON mock rule payloads and latency ranges. (Persisted locally in Room database).
- 📉 **DevTools Waterfall Timeline** — Visualize connection breakdowns (DNS lookup, TCP connect, TLS handshakes, TTFB waiting, and download times).
- 🔒 **Privacy & Redaction** — Automatically mask sensitive headers (e.g. `Authorization`) and recursive JSON keys (e.g. `password`, `token`) before storing logs.
- 🎛️ **Session Management** — Organize logs by debugging sessions, inspect request details, or purge individual sessions.
- 📋 **Export Utilities** — Export any network transaction on the fly as copyable **cURL** commands, single request **HAR** records, or full-session **HAR** archives.
- 🎨 **Modern Interface** — Clean dark-mode inspector layout with built-in JSON syntax highlighting and pretty-printing.

---

## 🏗️ Architecture

Tracea is split into modular libraries for clean multiplatform separation:

* **`tracea-core`** *(KMP)*: Core models, configurations, redaction engine, export formatters, and mock engine logic.
* **`tracea-storage`** *(KMP)*: Persistent SQLite logging database powered by Room KMP.
* **`tracea-manual`** *(KMP)*: Exposes platform-agnostic builders for custom request tracking.
* **`tracea`** *(KMP)*: Core public entry point facade.
* **`tracea-okhttp`** *(Android)*: Automatic request interceptor and EventListener adapters.
* **`tracea-ui`** *(Android)*: Beautiful dark-mode Compose inspector interface and floating activation overlay button.

---

## 🚀 Quick Start (Android)

### 1. Initialize Tracea
Initialize the SDK in your `Application` subclass:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Tracea.initialize(
            context = this,
            config = TraceaConfig(
                enabled = BuildConfig.DEBUG,
                showFloatingButton = true
            )
        )
    }
}
```

### 2. Attach Interceptor (OkHttp)
Simply add the interceptor to your `OkHttpClient` builder:

```kotlin
import com.hari.tracea.okHttpInterceptor

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(Tracea.okHttpInterceptor())
    .build()
```

---

## 🍏 Quick Start (iOS / Swift)

### 1. Build the Framework
On any Mac, compile the library into an Xcode framework:
```bash
./gradlew :tracea:assembleTraceaReleaseXCFramework
```
This outputs **`Tracea.xcframework`** in `tracea/build/XCFrameworks/release/`. Drag it into your Xcode target and select **Embed & Sign**.

### 2. Initialize in Swift
Initialize Tracea inside your iOS app delegate or SwiftUI App entry:

```swift
import SwiftUI
import Tracea

@main
struct TraceaDemoApp: App {
    init() {
        // Initialize Tracea. iOS handles file paths automatically under the hood.
        Tracea.shared.initialize(context: nil, config: TraceaConfig(enabled: true))
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

### 3. Log Requests manually
Perform your network tasks using `URLSession` and log metadata to the database:

```swift
import Tracea

// 1. Start tracking the call
let traceaCall = Tracea.shared.startRequest(method: "GET", url: "https://api.example.com/users")

// 2. Perform your URLSession request...
let task = URLSession.shared.dataTask(with: request) { data, response, error in
    if let error = error {
        // Log network failures
        traceaCall.failure(throwable: KotlinThrowable(message: error.localizedDescription))
    } else if let httpResponse = response as? HTTPURLResponse {
        let responseBody = String(data: data ?? Data(), encoding: .utf8) ?? ""
        
        // Log response data on completion
        traceaCall.response(
            statusCode: Int32(httpResponse.statusCode),
            headers: [:], // Map response headers here
            body: responseBody,
            contentType: "application/json"
        )
    }
}
task.resume()
```

---

## 📱 Interactive Demo Applications

To see Tracea in action, check out the pre-built demo projects:
* 🤖 **[Android Demo Client](file:///Users/hari/Documents/kids/Learning/Android/tracea/tracea-demo)**: Written in Kotlin & Jetpack Compose.
* 🍎 **[iOS SwiftUI Xcode Project Demo](file:///Users/hari/Documents/kids/Learning/Android/tracea/tracea-ios-demo)**: Pre-wired SwiftUI application using relative framework linking to instantly test Tracea logs and redactions on iOS Simulator.

---

## 📄 License

Licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.
