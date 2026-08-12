# Implementation Plan: Publish Android Network Debugger SDK to GitHub

This plan outlines the steps to prepare, configure, and publish the **Android Network Debugger SDK** to GitHub as an open-source Android library that can be easily integrated by developers via **JitPack**, **GitHub Packages**, or **Maven Central**.

---

## User Review Required

> [!IMPORTANT]
> To publish to GitHub, you will need a GitHub repository (e.g. `https://github.com/your-username/network-debugger`).
> We will set up your local project with all Gradle publishing scripts, JitPack configuration, GitHub Actions CI/CD workflows, `.gitignore`, Apache 2.0 license, and a complete open-source `README.md`.

---

## Proposed Changes

### Root Project & Build Configurations

#### [NEW] [.gitignore](file:///Users/hari/Documents/kids/Learning/Android/network-debugger/.gitignore)
- Ignore `.gradle/`, `build/`, `local.properties`, `.DS_Store`, `.idea/`, and temporary build outputs.

#### [NEW] [LICENSE](file:///Users/hari/Documents/kids/Learning/Android/network-debugger/LICENSE)
- Standard open-source Apache License 2.0.

#### [NEW] [jitpack.yml](file:///Users/hari/Documents/kids/Learning/Android/network-debugger/jitpack.yml)
- Configures Java 17 for JitPack automated builds:
```yaml
jdk:
  - openjdk17
before_install:
  - ./gradlew wrapper
```

#### [NEW] [README.md](file:///Users/hari/Documents/kids/Learning/Android/network-debugger/README.md)
- Complete open-source documentation:
  - Feature highlights (OkHttp automatic capture, Manual API, dark mode Compose UI, Room persistence, sensitive field redaction, cURL export).
  - Installation guide via Gradle (JitPack / GitHub Packages).
  - Quickstart code examples for `NetworkDebugger.initialize()` and `NetworkDebuggerInterceptor`.
  - Manual capture example (`NetworkDebugger.startManualRequest()`).
  - Configuration reference (`BodyCaptureConfig`, `RedactionConfig`, `StorageConfig`).
  - Architecture breakdown & module guide.

---

### Gradle Publishing Configuration

#### [MODIFY] [build.gradle.kts (root)](file:///Users/hari/Documents/kids/Learning/Android/network-debugger/build.gradle.kts)
- Apply `maven-publish` plugin configuration across library subprojects.
- Set publication coordinates: `group = "com.github.hari"`, `version = "1.0.0"`.

#### [MODIFY] [network-debugger/build.gradle.kts](file:///Users/hari/Documents/kids/Learning/Android/network-debugger/network-debugger/build.gradle.kts) & Library Submodules
- Enable single variant publishing (`publishing { singleVariant("release") { withSourcesJar() } }`) for AAR generation.

---

### CI/CD Workflows

#### [NEW] [.github/workflows/ci.yml](file:///Users/hari/Documents/kids/Learning/Android/network-debugger/.github/workflows/ci.yml)
- Automatically runs Gradle build and unit tests on pull requests and pushes to `main`.

#### [NEW] [.github/workflows/publish.yml](file:///Users/hari/Documents/kids/Learning/Android/network-debugger/.github/workflows/publish.yml)
- Automated publishing workflow to build AARs and create a GitHub Release with attached library artifacts when a release tag (e.g. `v1.0.0`) is created.

---

## Verification Plan

### Automated Verification
1. Run `./gradlew publishToMavenLocal` to verify that all AAR artifacts (`network-debugger`, `network-debugger-core`, `network-debugger-okhttp`, etc.) and POM metadata build cleanly into local Maven repository (`~/.m2/repository`).
2. Run `./gradlew assembleRelease` to confirm production release build.

### Git Verification
1. Run `git status` to verify clean tracking.
2. Provide simple terminal commands for the user to push to their remote GitHub repository.
