# 📱 Network Debugger iOS Demo App

This SwiftUI application demonstrates the **Network Debugger SDK** running natively on **iOS** using `URLSession` network interception.

---

## ✨ Features Demonstrated

1. **GET Request Inspection**: Sends `https://httpbin.org/get` and logs status code and duration.
2. **POST Request & Redaction**: Sends `https://httpbin.org/post` with sensitive headers (`Authorization`) and JSON tokens to demonstrate security redaction.
3. **404 / 500 Error Tracing**: Triggers HTTP client & server error codes to verify error badge rendering.
4. **URLSession Interception**: Uses `URLProtocol` to capture network calls transparently.

---

## 🚀 How to Run in Xcode

1. Open Xcode and select **File -> Open**.
2. Select the `network-debugger-demo-ios` directory.
3. Select an iOS Simulator (e.g. `iPhone 16 Pro`) and press **`⌘ R`** to run!
