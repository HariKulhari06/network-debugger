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

// Swift URLProtocol interceptor for native iOS URLSession network calls
public class NetworkDebuggerURLProtocol: URLProtocol {
    private static let handledKey = "NetworkDebuggerURLProtocolHandledKey"
    
    public override class func canInit(with request: URLRequest) -> Bool {
        if URLProtocol.property(forKey: handledKey, in: request) != nil {
            return false
        }
        return true
    }
    
    public override class func canonicalRequest(for request: URLRequest) -> URLRequest {
        return request
    }
    
    public override func startLoading() {
        guard let mutableRequest = (request as NSURLRequest).mutableCopy() as? NSMutableURLRequest else {
            client?.urlProtocolDidFinishLoading(self)
            return
        }
        
        URLProtocol.setProperty(true, forKey: NetworkDebuggerURLProtocol.handledKey, in: mutableRequest)
        
        let session = URLSession(configuration: .ephemeral)
        let task = session.dataTask(with: mutableRequest as URLRequest) { [weak self] data, response, error in
            guard let self = self else { return }
            
            if let response = response {
                self.client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
            }
            if let data = data {
                self.client?.urlProtocol(self, didLoad: data)
            }
            if let error = error {
                self.client?.urlProtocol(self, didFailWithError: error)
            } else {
                self.client?.urlProtocolDidFinishLoading(self)
            }
        }
        task.resume()
    }
    
    public override func stopLoading() {}
}
