# 📡 Tracea for Android

<p align="center">
  <img src="logo.png" width="180" alt="Tracea Logo"/>
</p>

[![JitPack](https://jitpack.io/v/HariKulhari06/tracea.svg)](https://jitpack.io/#HariKulhari06/tracea)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-purple.svg)](https://kotlinlang.org)

**Tracea** is a lightweight, Chrome DevTools / Proxyman-style in-app network inspection SDK embedded directly inside your **Android** application.

It automatically intercepts network calls made via **OkHttp**, provides a **manual capture API** for custom network stacks, redacts sensitive headers and JSON payloads, persists history via Room DB, and displays a Jetpack Compose developer-friendly dark-theme UI.

---

## 📱 Screenshots & Demo Video

| Network Event Inspector | Request Details & cURL |
| :---: | :---: |
| <img src="screenshot/network_list.png" width="360" alt="Network List Screen"/> | <img src="screenshot/request_detail.png" width="360" alt="Request Detail Screen"/> |

> 📹 **Video File**: [`screenshot/demo.mp4`](screenshot/demo.mp4)

---

## ✨ Features

- 🔍 **Network Inspection** — View all HTTP requests/responses in real-time
- 🎭 **Mock Rules** — Define mock responses with path matching, status codes, and delays
- 🔒 **Redaction** — Automatically redact sensitive headers and JSON fields
- 📋 **cURL Export** — Copy any request as a cURL command
- 📊 **HAR Export** — Export network sessions in HAR format
- ⏱️ **Timing Details** — DNS, connect, TLS, waiting, and download breakdowns
- 🎨 **Dark Theme UI** — Premium Jetpack Compose interface
- 🔌 **Manual Capture API** — Support for non-OkHttp network stacks

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
    debugImplementation("com.github.HariKulhari06:tracea:1.0.0")
}
```

---

## 🚀 Quickstart Guide

### 1. Initialize in Application Class

```kotlin
class DemoApplication : Application() {
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

### 2. Attach Interceptor to OkHttpClient

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(Tracea.interceptor)
    .eventListenerFactory(Tracea.timingEventListenerFactory)
    .build()
```

---

## ✍️ Manual Capture API

For networking stacks that cannot use OkHttp interceptors (e.g., custom sockets, WebSockets, or legacy HTTPURLConnection):

```kotlin
val manualCall = Tracea.startManualRequest(
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
tracea/
├── tracea/                  # 🤖 Native Android SDK Facade
├── tracea-core/             # 🤖 Native Android Core Models & Pipeline
├── tracea-okhttp/           # 🤖 Native Android OkHttp Interceptor
├── tracea-manual/           # 🤖 Native Android Manual API
├── tracea-storage/          # 🤖 Native Android Room Storage
├── tracea-ui/               # 🤖 Native Android Jetpack Compose UI
└── tracea-demo/             # 📱 Native Android Demo App
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
