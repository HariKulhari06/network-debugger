# Implementation Plan & Technical Reference: Network Debugger SDK (Android Native Only)

This document contains the complete technical architecture, module mapping, build commands, dependency coordinates, and feature roadmap for the **Network Debugger SDK**.

---

## 1. Project Overview & Multi-Module Architecture

The project consists of a pure native Android library suite (`.aar`) targeting OkHttp, Room DB, and Jetpack Compose UI.

Submodules:
1. **`network-debugger-core`**: Baseline models (`NetworkEvent`, `HttpMethod`, `BodyData`, `NetworkTiming`, `NetworkError`), `RedactionEngine` (case-insensitive headers + recursive JSON field redaction), `NetworkEventCollector` (`SharedFlow`), size & duration formatters, cURL command generator.
2. **`network-debugger-okhttp`**: `NetworkDebuggerInterceptor` (OkHttp 4.x), `OkHttpBodyExtractor` (stream-safe peekBody), `OkHttpTimingCapture` (`EventListener.Factory` measuring DNS, TLS, TCP, TTFB, download times).
3. **`network-debugger-manual`**: Builder API (`ManualNetworkCall`) and factory (`ManualCaptureApi`) for logging custom HTTP clients, sockets, or WebSockets.
4. **`network-debugger-storage`**: Room DB (`NetworkEventDatabase`), lean entities (`NetworkEventEntity`), DAO, disk file storage (`BodyFileStorage` for payloads >4KB), `RoomNetworkEventStore`, and `MemoryNetworkEventStore`.
5. **`network-debugger-ui`**: Jetpack Compose dark-theme UI:
   - `NetworkListScreen`: Live status filters (`All`, `2xx`, `3xx`, `4xx`, `5xx`, `Errors`), debounced keyword search, two-line request rows.
   - `RequestDetailScreen`: Overview, Request, Response (syntax-highlighted JSON viewer), and Timing tabs with visual latency duration bars & cURL export.
   - `TimelineScreen`: Chronological dot-connector canvas UI, status legend, and session duration/slowest request stats.
   - `SettingsScreen`: Toggles for capture, floating overlay button, custom sensitive redaction rules, max storage retention limits.
   - `FloatingDebugButton`: Draggable overlay pill with live request counter.
6. **`network-debugger`**: Facade singleton (`NetworkDebugger`) unifying initialization, storage, overlay launcher, and interceptor providers.
7. **`network-debugger-demo`**: Showcase Android application with GET, POST, PUT, DELETE, 404, 500, timeout, and manual capture triggers.

---

## 2. Published Artifact Coordinates & Repository Details

- **GitHub Repository**: [`https://github.com/HariKulhari06/network-debugger`](https://github.com/HariKulhari06/network-debugger)
- **Maven Group**: `com.github.HariKulhari06`
- **Latest Release Version**: `1.2.0` (Git Tag: `v1.2.0`)

### Dependency Integration:

#### Settings Configuration (`settings.gradle.kts`)
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

#### Native Android App Integration
```kotlin
dependencies {
    debugImplementation("com.github.HariKulhari06:network-debugger:1.2.0")
}
```

---

## 3. Build & Test Verification Commands

To build all submodules cleanly on JDK 17 (Android SDK path: `/Users/hari/Library/Android/sdk`):

```bash
JAVA_HOME="/Users/hari/Library/Java/JavaVirtualMachines/jbr-17.0.14/Contents/Home" ./gradlew assembleDebug --console=plain
```

To build and test local Maven publication:

```bash
JAVA_HOME="/Users/hari/Library/Java/JavaVirtualMachines/jbr-17.0.14/Contents/Home" ./gradlew publishToMavenLocal --console=plain
```

---

## 4. Key File Map

| Path | Purpose |
| :--- | :--- |
| `network-debugger/src/main/kotlin/.../NetworkDebugger.kt` | Native Android SDK facade |
| `network-debugger-core/src/main/kotlin/.../redaction/RedactionEngine.kt` | Recursive JSON & Header redaction engine |
| `network-debugger-okhttp/src/main/kotlin/.../NetworkDebuggerInterceptor.kt` | OkHttp 4.x request/response interceptor |
| `network-debugger-manual/src/main/kotlin/.../ManualNetworkCall.kt` | Builder API for manual logging |
| `network-debugger-storage/src/main/kotlin/.../RoomNetworkEventStore.kt` | Room DB & disk body storage engine |
| `network-debugger-ui/src/main/kotlin/.../NetworkDebuggerActivity.kt` | Inspector Activity & Navigation Host |
| `.github/workflows/ci.yml` | GitHub Actions CI build & test workflow |
| `.github/workflows/publish.yml` | GitHub Actions automated release & AAR publisher |

---

## 5. Future Enhancement Roadmap for Next Agents

1. **`network-debugger-noop` Module**: Create a 0-byte stub library variant for production release builds.
2. **HAR File Export (`.har`)**: Add HAR v1.2 export function in UI for import into Charles Proxy / Proxyman.
3. **Response Mocking Engine**: Add UI controls to override status codes and stub JSON responses on device.
