import Foundation

@MainActor
class NetworkDemoService: ObservableObject {
    @Published var logs: [String] = []
    @Published var isLoading: Boolean = false
    
    private let session: URLSession
    
    init() {
        let config = URLSessionConfiguration.default
        // Protocol interception for URLSession
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
        guard let url = URL(string: "https://httpbin.org/get") else { return }
        
        session.dataTask(with: url) { data, response, error in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                    self.appendLog("✅ GET Succeeded: Status \(httpResponse.statusCode)")
                } else if let error = error {
                    self.appendLog("❌ GET Failed: \(error.localizedDescription)")
                }
            }
        }.resume()
    }
    
    func executePost() {
        appendLog("🚀 Sending POST https://httpbin.org/post...")
        guard let url = URL(string: "https://httpbin.org/post") else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer sample_token_12345", forHTTPHeaderField: "Authorization")
        
        let json: [String: Any] = [
            "user_id": 42,
            "action": "checkout",
            "token": "secret_access_token_abc"
        ]
        request.httpBody = try? JSONSerialization.data(withJSONObject: json)
        
        session.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                    self.appendLog("✅ POST Succeeded: Status \(httpResponse.statusCode)")
                } else if let error = error {
                    self.appendLog("❌ POST Failed: \(error.localizedDescription)")
                }
            }
        }.resume()
    }
    
    func executeError404() {
        appendLog("🚀 Sending GET https://httpbin.org/status/404...")
        guard let url = URL(string: "https://httpbin.org/status/404") else { return }
        
        session.dataTask(with: url) { data, response, error in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                    self.appendLog("⚠️ 404 Response Received: Status \(httpResponse.statusCode)")
                }
            }
        }.resume()
    }
    
    func executeError500() {
        appendLog("🚀 Sending GET https://httpbin.org/status/500...")
        guard let url = URL(string: "https://httpbin.org/status/500") else { return }
        
        session.dataTask(with: url) { data, response, error in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                    self.appendLog("❌ 500 Response Received: Status \(httpResponse.statusCode)")
                }
            }
        }.resume()
    }
}
