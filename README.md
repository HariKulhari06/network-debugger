# 🌐 Network Debugger for Android

[![JitPack](https://jitpack.io/v/HariKulhari06/network-debugger.svg)](https://jitpack.io/#HariKulhari06/network-debugger)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-purple.svg)](https://kotlinlang.org)

**Network Debugger** is a lightweight, Chrome DevTools / Proxyman-style in-app network inspection SDK embedded directly inside your **Android** application.

It automatically intercepts network calls made via **OkHttp**, provides a **manual capture API** for custom network stacks, redacts sensitive headers and JSON payloads, persists history via Room DB, and displays a Jetpack Compose developer-friendly dark-theme UI.

---

## 📱 Screenshots & Demo Video

| Network Event Inspector | Request Details & cURL |
| :---: | :---: |
| <img src="screenshot/network_list.png" width="360" alt="Network List Screen"/> | <img src="screenshot/request_detail.png" width="360" alt="Request Detail Screen"/> |

> 📹 **Video File**: [`screenshot/demo.mp4`](screenshot/demo.mp4)

---

## 📦 Installation

Add **JitPack** to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
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
    // Pure native Android SDK
    debugImplementation("com.github.HariKulhari06:network-debugger:1.0.1")
}
```

---

## 🚀 Quickstart Guide

### 1. Initialize in Application Class

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

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(NetworkDebugger.interceptor)
    .eventListenerFactory(NetworkDebugger.timingEventListenerFactory)
    .build()
```

---

## 🎭 API Mocking

Network Debugger includes a powerful mocking engine that allows you to intercept network calls and return custom responses without changing your backend.

### Define Mock Rules

You can add mock rules programmatically or via the in-app UI:

```kotlin
NetworkDebugger.addMockRule(
    MockRule(
        id = UUID.randomUUID().toString(),
        pathPattern = "/v1/profile",
        method = HttpMethod.GET,
        statusCode = 200,
        responseBody = """{"name": "Mock User", "email": "mock@example.com"}""",
        delayMs = 1000 // Simulate network latency
    )
)
```

### Toggle Mocking

Enable or disable all mocks globally:

```kotlin
NetworkDebugger.setMockingEnabled(true)
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
```

---

## 🛠 Project Architecture

```text
network-debugger/
├── network-debugger/              # 🤖 Native Android SDK Facade
├── network-debugger-core/         # 🤖 Native Android Core Models & Pipeline
├── network-debugger-okhttp/       # 🤖 Native Android OkHttp Interceptor
├── network-debugger-manual/       # 🤖 Native Android Manual API
├── network-debugger-storage/      # 🤖 Native Android Room Storage
├── network-debugger-ui/           # 🤖 Native Android Jetpack Compose UI
└── network-debugger-demo/         # 📱 Native Android Demo App
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
