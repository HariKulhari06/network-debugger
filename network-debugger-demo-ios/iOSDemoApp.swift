import SwiftUI

@main
struct iOSDemoApp: App {
    init() {
        // Register URLProtocol interception for native URLSession calls
        URLProtocol.registerClass(NetworkDebuggerURLProtocol.self)
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

// Fallback stub for standalone Xcode compilation preview
class NetworkDebuggerURLProtocol: URLProtocol {
    override class func canInit(with request: URLRequest) -> Bool { return false }
}
