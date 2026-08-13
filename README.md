# 📡 Tracea

<p align="center">
  <img src="logo.png" width="160" alt="Tracea Logo"/>
</p>

<p align="center">
  <a href="https://jitpack.io/#HariKulhari06/tracea"><img src="https://jitpack.io/v/HariKulhari06/tracea.svg" alt="JitPack"/></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License"/></a>
  <a href="https://developer.android.com"><img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform"/></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.1.20-purple.svg" alt="Kotlin"/></a>
</p>

**Tracea** is a modern, high-performance in-app network debugger for Android. Inspired by tools like Proxyman and Charles, it provides a seamless way to inspect, mock, and analyze network traffic directly within your application.

---

## 📱 Visual Overview

| Network Inspector | Mocking Rules | Detailed Analysis |
| :---: | :---: | :---: |
| <img src="screenshot/Screenshot_20260813_093825.png" width="280" alt="Network List"/> | <img src="screenshot/Screenshot_20260813_093838.png" width="280" alt="Mock Rules"/> | <img src="screenshot/Screenshot_20260813_093900.png" width="280" alt="Request Detail"/> |

---

## ✨ Features

- 🔍 **Real-time Inspection** — Monitor HTTP/HTTPS traffic with deep request/response analysis.
- 🎭 **Dynamic Mocking** — Intercept and modify responses with path matching, status codes, and custom delays.
- 🔒 **Privacy First** — Built-in redaction for sensitive headers and JSON keys (auth tokens, passwords, etc.).
- 📋 **Developer Tools** — Export to **cURL** or **HAR** format for easy debugging in external tools.
- ⏱️ **Precision Timing** — Detailed breakdown of DNS, TLS handshake, and transfer times.
- 🎨 **Modern UI** — A premium, minimalist dark-theme interface built entirely with Jetpack Compose.
- 🔌 **Universal Support** — First-class OkHttp integration + Manual Capture API for any network stack.

---

## 📦 Installation

Add **JitPack** to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    debugImplementation("com.github.HariKulhari06:tracea:1.0.0")
}
```

---

## 🚀 Quick Start

### 1. Initialize
```kotlin
Tracea.initialize(
    context = this,
    config = TraceaConfig(
        enabled = BuildConfig.DEBUG,
        showFloatingButton = true
    )
)
```

### 2. Plug and Play (OkHttp)
```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(Tracea.interceptor)
    .eventListenerFactory(Tracea.timingEventListenerFactory)
    .build()
```

---

## 🛡️ Advanced: Manual Capture
For non-OkHttp stacks (WebSockets, legacy libraries, or custom sockets):

```kotlin
val call = Tracea.startManualRequest("POST", "https://api.example.com/v1")
call.requestHeaders(mapOf("Auth" to "redacted"))
    .response(200, body = "{\"status\":\"ok\"}")
```

---

## 🏗️ Architecture

Tracea is built as a modular system for maximum flexibility:
- **Core**: Logic, models, and processing pipeline.
- **Storage**: Persistent history via Room DB.
- **UI**: Modern Jetpack Compose debugging interface.
- **Adapters**: specialized plugins for OkHttp and manual capturing.

---

## 📄 License
Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.
