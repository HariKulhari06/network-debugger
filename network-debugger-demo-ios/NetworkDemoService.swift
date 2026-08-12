import Foundation

struct CapturedNetworkEvent: Identifiable {
    let id: String
    let timestamp: String
    let method: String
    let url: String
    let statusCode: Int
    let durationMs: Int
    val requestHeaders: [String: String]
    val responseHeaders: [String: String]
    val requestBody: String?
    val responseBody: String?
    val curlCommand: String
}

@MainActor
class NetworkDemoService: ObservableObject {
    @Published var logs: [String] = []
    @Published var capturedEvents: [CapturedNetworkEvent] = []
    @Published var isDebuggerSheetPresented: Bool = false
    
    private let session: URLSession
    
    init() {
        let config = URLSessionConfiguration.default
        session = URLSession(configuration: config)
    }
    
    func appendLog(_ text: String) {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm:ss"
        let timestamp = formatter.string(from: Date())
        logs.insert("[\(timestamp)] \(text)", at: 0)
    }
    
    func executeGet() {
        appendLog("🚀 Sending GET https://httpbin.org/get...")
        let startTime = Date()
        guard let url = URL(string: "https://httpbin.org/get") else { return }
        
        session.dataTask(with: url) { data, response, error in
            let duration = Int(Date().timeIntervalSince(startTime) * 1000)
            DispatchQueue.main.async {
                let status = (response as? HTTPURLResponse)?.statusCode ?? 200
                self.appendLog("✅ GET Succeeded: Status \(status) (\(duration)ms)")
                
                let responseText = data != nil ? String(data: data!, encoding: .utf8) : nil
                self.recordEvent(
                    method: "GET",
                    url: "https://httpbin.org/get",
                    statusCode: status,
                    durationMs: duration,
                    requestHeaders: ["Accept": "application/json", "User-Agent": "NetworkDebugger-iOS/1.0"],
                    responseHeaders: ["Content-Type": "application/json"],
                    requestBody: nil,
                    responseBody: responseText
                )
            }
        }.resume()
    }
    
    func executePost() {
        appendLog("🚀 Sending POST https://httpbin.org/post...")
        let startTime = Date()
        guard let url = URL(string: "https://httpbin.org/post") else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer [REDACTED_SECRET_TOKEN]", forHTTPHeaderField: "Authorization")
        
        let reqBody = """
        {
          "user_id": 42,
          "action": "checkout",
          "access_token": "[REDACTED]",
          "card_number": "****-****-****-4242"
        }
        """
        request.httpBody = reqBody.data(using: .utf8)
        
        session.dataTask(with: request) { data, response, error in
            let duration = Int(Date().timeIntervalSince(startTime) * 1000)
            DispatchQueue.main.async {
                let status = (response as? HTTPURLResponse)?.statusCode ?? 200
                self.appendLog("✅ POST Succeeded: Status \(status) (\(duration)ms)")
                
                let responseText = data != nil ? String(data: data!, encoding: .utf8) : nil
                self.recordEvent(
                    method: "POST",
                    url: "https://httpbin.org/post",
                    statusCode: status,
                    durationMs: duration,
                    requestHeaders: ["Content-Type": "application/json", "Authorization": "Bearer [REDACTED]"],
                    responseHeaders: ["Content-Type": "application/json"],
                    requestBody: reqBody,
                    responseBody: responseText
                )
            }
        }.resume()
    }
    
    func executeError404() {
        appendLog("🚀 Sending GET https://httpbin.org/status/404...")
        let startTime = Date()
        guard let url = URL(string: "https://httpbin.org/status/404") else { return }
        
        session.dataTask(with: url) { data, response, error in
            let duration = Int(Date().timeIntervalSince(startTime) * 1000)
            DispatchQueue.main.async {
                let status = (response as? HTTPURLResponse)?.statusCode ?? 404
                self.appendLog("⚠️ 404 Response Received: Status \(status)")
                
                self.recordEvent(
                    method: "GET",
                    url: "https://httpbin.org/status/404",
                    statusCode: status,
                    durationMs: duration,
                    requestHeaders: ["Accept": "application/json"],
                    responseHeaders: ["Content-Type": "text/html"],
                    requestBody: nil,
                    responseBody: "404 Not Found"
                )
            }
        }.resume()
    }
    
    func executeError500() {
        appendLog("🚀 Sending GET https://httpbin.org/status/500...")
        let startTime = Date()
        guard let url = URL(string: "https://httpbin.org/status/500") else { return }
        
        session.dataTask(with: url) { data, response, error in
            let duration = Int(Date().timeIntervalSince(startTime) * 1000)
            DispatchQueue.main.async {
                let status = (response as? HTTPURLResponse)?.statusCode ?? 500
                self.appendLog("❌ 500 Response Received: Status \(status)")
                
                self.recordEvent(
                    method: "GET",
                    url: "https://httpbin.org/status/500",
                    statusCode: status,
                    durationMs: duration,
                    requestHeaders: ["Accept": "application/json"],
                    responseHeaders: ["Content-Type": "text/html"],
                    requestBody: nil,
                    responseBody: "500 Internal Server Error"
                )
            }
        }.resume()
    }
    
    private func recordEvent(
        method: String,
        url: String,
        statusCode: Int,
        durationMs: Int,
        requestHeaders: [String: String],
        responseHeaders: [String: String],
        requestBody: String?,
        responseBody: String?
    ) {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm:ss.SSS"
        let timestamp = formatter.string(from: Date())
        let id = UUID().uuidString
        
        var curl = "curl -X \(method) \"\(url)\""
        for (key, val) in requestHeaders {
            curl += " -H \"\(key): \(val)\""
        }
        if let body = requestBody {
            curl += " -d '\(body)'"
        }
        
        let event = CapturedNetworkEvent(
            id: id,
            timestamp: timestamp,
            method: method,
            url: url,
            statusCode: statusCode,
            durationMs: durationMs,
            requestHeaders: requestHeaders,
            responseHeaders: responseHeaders,
            requestBody: requestBody,
            responseBody: responseBody,
            curlCommand: curl
        )
        
        capturedEvents.insert(event, at: 0)
    }
    
    func clearEvents() {
        capturedEvents.removeAll()
        logs.removeAll()
    }
}
