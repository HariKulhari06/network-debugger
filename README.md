# 🌐 Network Debugger for Android

[![JitPack](https://jitpack.io/v/HariKulhari06/network-debugger.svg)](https://jitpack.io/#HariKulhari06/network-debugger)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-purple.svg)](https://kotlinlang.org)

**Network Debugger** is a lightweight, Chrome DevTools / Proxyman-style in-app network inspection SDK embedded directly inside your Android application.

It automatically intercepts network calls made via **OkHttp**, provides a **manual capture API** for custom network stacks, redacts sensitive headers and JSON payloads, persists history via Room DB, and displays a Jetpack Compose developer-friendly dark-theme UI.

---

## 📱 Screenshots & Demo

| Network Event Inspector | Request Details & cURL |
| :---: | :---: |
| <img src="screenshots/network_list.png" width="360" alt="Network List Screen"/> | <img src="screenshots/request_detail.png" width="360" alt="Request Detail Screen"/> |

### 🎬 Demo Video
Place your recorded demo video at `screenshots/demo_video.mp4`:
- 📹 [Watch Demo Video](screenshots/demo_video.mp4)

---

## ✨ Key Features

- **🚀 OkHttp Auto Interception**: Zero-setup interception via standard `OkHttp` interceptor (`NetworkDebuggerInterceptor`).
- **⏱ Granular Timing Breakdown**: Captures DNS lookup, TCP connect, TLS handshake, TTFB waiting, and download times using OkHttp `EventListener`.
- **✍️ Manual Capture API**: Builder API (`ManualNetworkCall`) to log requests from custom HTTP clients, Ktor, Volley, or GraphQL.
- **🎨 Sleek Compose UI**: Modern dark-theme developer UI with status filter chips (`All`, `2xx`, `3xx`, `4xx`, `5xx`, `Errors`), keyword search, and interactive cURL export.
- **🔒 Recursive Redaction Engine**: Automatically obfuscates tokens, authorization headers, passwords, and sensitive keys before storing or displaying (`[REDACTED]`).
- **💾 Hybrid Room & File Storage**: Stores inline metadata in Room DB and large bodies (>4KB) as file references in app cache with configurable retention limits.
- **🌐 Floating Debug Button**: Optional draggable overlay button with real-time network request counters for instant debugging.

---

## 📦 Installation

Add **JitPack** to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your app module's `build.gradle.kts`:

```kotlin
dependencies {
    // Complete SDK (Core + OkHttp + Storage + Compose UI)
    debugImplementation("com.github.HariKulhari06:network-debugger:1.0.0")

    // Modular components (optional)
    debugImplementation("com.github.HariKulhari06:network-debugger-okhttp:1.0.0")
    debugImplementation("com.github.HariKulhari06:network-debugger-manual:1.0.0")
}
```

---

## 🚀 Quickstart Guide

### 1. Initialize in Application Class

Initialize the SDK inside your `Application` class:

```kotlin
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        NetworkDebugger.initialize(
            context = this,
            config = NetworkDebuggerConfig(
                enabled = BuildConfig.DEBUG,
                showFloatingButton = true
            )
        )
    }
}
```

### 2. Attach Interceptor to OkHttpClient

Add `NetworkDebuggerInterceptor` to your `OkHttpClient.Builder`:

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(NetworkDebugger.interceptor)
    .eventListenerFactory(NetworkDebugger.timingEventListenerFactory)
    .build()
```

### 3. Open Debugger UI Programmatically

Launch the in-app inspector anywhere in your debug activity:

```kotlin
// Open the Network Debugger activity directly
NetworkDebugger.show(context)
```

---

## ✍️ Manual Capture API

For networking stacks that cannot use OkHttp interceptors (e.g., custom sockets, WebSockets, or legacy HTTPURLConnection):

```kotlin
val manualCall = NetworkDebugger.startManualRequest(
    method = "POST",
    url = "https://api.example.com/v1/checkout"
)

manualCall.requestHeaders(mapOf("Authorization" to "Bearer token123"))
         .requestBody("""{"item_id": 42, "quantity": 1}""", "application/json")

// On response received:
manualCall.response(
    statusCode = 200,
    headers = mapOf("Content-Type" to "application/json"),
    body = """{"status": "success", "order_id": "ORD-9912"}""",
    contentType = "application/json"
)

// Or on network failure:
// manualCall.failure(throwable)
```

---

## ⚙️ Custom Configuration

Tailor storage capacity, payload capture limits, and security redaction rules:

```kotlin
val config = NetworkDebuggerConfig(
    enabled = true,
    showFloatingButton = true,
    bodyCaptureConfig = BodyCaptureConfig(
        maxRequestBodySize = 1L * 1024 * 1024,  // 1 MB limit
        maxResponseBodySize = 2L * 1024 * 1024, // 2 MB limit
        captureBinary = false
    ),
    storageConfig = StorageConfig(
        maxRequests = 500 // Keeps last 500 requests
    ),
    redactionConfig = RedactionConfig(
        sensitiveHeaders = setOf("Authorization", "Cookie", "X-API-Key"),
        sensitiveJsonFields = setOf("password", "access_token", "refresh_token", "secret", "client_secret")
    )
)

NetworkDebugger.initialize(context, config)
```

---

## 🛠 Project Architecture

The library is built with a modular 2026 Android architecture:

```text
network-debugger/
├── network-debugger-core/     # Domain models, pipeline, redaction engine & utilities
├── network-debugger-okhttp/   # OkHttp interceptor & timing capture listeners
├── network-debugger-manual/   # Builder-style API for manual network logging
├── network-debugger-storage/  # Room database & disk file storage
├── network-debugger-ui/       # Jetpack Compose dark-theme inspector UI & screens
├── network-debugger/          # Unified SDK facade (Singleton entry point)
└── network-debugger-demo/     # Showcase Android application
```

---

## 📄 License

```text
Copyright 2026 Hari

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations
under the License.
```
