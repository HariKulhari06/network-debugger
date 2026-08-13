import Foundation
import Tracea

class NetworkManager {
    static let shared = NetworkManager()

    private init() {}

    /**
     * Executes an HTTP request and logs it to Tracea.
     */
    func performRequest(
        method: String,
        urlString: String,
        body: String? = nil,
        headers: [String: String]? = nil,
        completion: @escaping (String) -> Void
    ) {
        guard let url = URL(string: urlString) else {
            completion("Invalid URL: \(urlString)")
            return
        }

        // 1. Start tracking the manual request in Tracea
        let traceaCall = Tracea.shared.startRequest(method: method, url: urlString)

        var request = URLRequest(url: url)
        request.httpMethod = method

        // 2. Set headers
        if let headers = headers {
            for (key, value) in headers {
                request.setValue(value, forHTTPHeaderField: key)
            }
            traceaCall.requestHeaders(headers: headers)
        }

        // 3. Set body
        if let body = body {
            request.httpBody = body.data(using: .utf8)
            traceaCall.requestBody(body: body, contentType: headers?["Content-Type"] ?? "text/plain")
        }

        // 4. Fire standard URLSession data task
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            let responseString: String

            if let error = error {
                responseString = "Error: \(error.localizedDescription)"
                
                // Log failure to Tracea
                traceaCall.failure(throwable: KotlinThrowable(message: error.localizedDescription))
            } else if let httpResponse = response as? HTTPURLResponse {
                let dataString = data.flatMap { String(data: $0, encoding: .utf8) } ?? ""
                responseString = "Status Code: \(httpResponse.statusCode)\n\nResponse:\n\(dataString)"

                // Convert response headers
                var respHeaders: [String: String] = [:]
                httpResponse.allHeaderFields.forEach { key, value in
                    if let k = key as? String, let v = value as? String {
                        respHeaders[k] = v
                    }
                }

                // Log response details to Tracea
                traceaCall.response(
                    statusCode: Int32(httpResponse.statusCode),
                    headers: respHeaders,
                    body: dataString,
                    contentType: respHeaders["Content-Type"] ?? "text/plain"
                )
            } else {
                responseString = "Unknown Response"
            }

            DispatchQueue.main.async {
                completion(responseString)
            }
        }
        task.resume()
    }
}
