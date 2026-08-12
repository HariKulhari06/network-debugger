# Implementation Plan: Kotlin Multiplatform (KMP) Network Debugger SDK (Android & iOS)

This document outlines the strategy for building a **Kotlin Multiplatform (KMP)** Network Debugger library (`network-debugger-kmp`) that runs seamlessly on both **Android** and **iOS** with shared core logic, network capture engines, and a unified **Compose Multiplatform** UI.

---

## Technical Architecture Overview

```text
network-debugger-kmp/
├── commonMain/                     # Shared Kotlin Multiplatform Logic
│   ├── model/                      # NetworkEvent, HttpMethod, BodyData, NetworkTiming
│   ├── redaction/                  # RedactionEngine (kotlinx.serialization)
│   ├── ktor/                       # Ktor Client NetworkDebuggerKtorPlugin (Android & iOS)
│   ├── manual/                     # Shared Manual Capture API
│   ├── store/                      # Room KMP Database & Memory Store
│   └── ui/                         # Compose Multiplatform Dark Theme UI (Android & iOS)
│
├── androidMain/                    # Android-Specific Extensions
│   ├── okhttp/                     # OkHttp Interceptor & EventListener
│   └── platform/                   # Android Activity & Notification/Overlay helpers
│
└── iosMain/                        # iOS-Specific Extensions
    ├── urlsession/                 # NSURLProtocol / URLSession Interceptor
    └── platform/                   # UIViewController wrapper & Swift export (Framework)
```

---

## Key KMP Integration Components

### 1. Ktor Client Plugin (`commonMain`)
Intersects network calls on both Android & iOS when using Ktor (`io.ktor:ktor-client-*`):

```kotlin
// commonMain
val client = HttpClient {
    install(NetworkDebuggerKtorPlugin)
}
```

### 2. Native Network Interceptors
- **Android (`androidMain`)**: `NetworkDebuggerInterceptor` for OkHttp.
- **iOS (`iosMain`)**: `NSURLProtocol` subclass (`NetworkDebuggerURLProtocol`) that automatically captures all native iOS `URLSession` / Alamofire / Moya network calls.

### 3. Database Persistence (Room KMP)
- Room now supports Kotlin Multiplatform targeting both Android (`SQLiteDatabase`) and iOS (`sqlite3`).

### 4. Compose Multiplatform UI (`commonMain`)
- The entire developer UI (Network List, Request Details, Timeline, cURL export, Settings) runs natively on both **Android** (Activity/Composable) and **iOS** (`UIViewController` / SwiftUI view wrapper `NetworkDebuggerView()`).

---

## Proposed KMP Module Structure

### Submodules:
1. `network-debugger-core` (`commonMain`): Shared models, pipeline, redaction, Ktor plugin, Room DB.
2. `network-debugger-okhttp` (`androidMain`): OkHttp interceptor for Android apps.
3. `network-debugger-ios` (`iosMain`): `NSURLProtocol` interceptor for native iOS apps.
4. `network-debugger-ui` (`commonMain`): Compose Multiplatform UI for Android & iOS.
5. `network-debugger` (Facade): Unified Multiplatform entry point.

---

## Verification Plan

### Automated Verification
1. `./gradlew :network-debugger-core:compileKotlinIosX64` & `compileKotlinAndroid`
2. Run unit tests on both JVM (`desktopTest` / `androidTest`) and iOS simulator (`iosX64Test`).

### iOS Verification
1. Generate XCFramework (`./gradlew assembleNetworkDebuggerXCFramework`).
2. Import XCFramework into an Xcode iOS Swift project and test `URLSession` interception.
