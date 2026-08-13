import SwiftUI
import Tracea

struct ContentView: View {
    @State private var logOutput: String = "Tap any API option to trigger HTTP request..."
    @State private var isLoading: Boolean = false

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Header Banner
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Tracea Network Inspector")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(.white)
                        Text("KMP iOS SwiftUI Demo Client")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(Color.white.opacity(0.8))
                    }
                    Spacer()
                    Image(systemName: "bolt.shield.fill")
                        .font(.system(size: 28))
                        .foregroundColor(.yellow)
                }
                .padding()
                .background(LinearGradient(gradient: Gradient(colors: [Color.blue, Color.purple]), startPoint: .topLeading, endPoint: .bottomTrailing))

                // Scrollable Actions Panel
                ScrollView {
                    VStack(spacing: 16) {
                        // SECTION 1: Standard Requests
                        CategorySection(title: "STANDARD SCENARIOS") {
                            ActionButton(title: "GET JSON Data (Users List)", icon: "person.3.fill", color: .blue) {
                                triggerGetUsers()
                            }
                            ActionButton(title: "POST Secure Login (Body Redaction)", icon: "lock.shield.fill", color: .green) {
                                triggerPostLogin()
                            }
                            ActionButton(title: "PUT Update Profile", icon: "pencil.and.outline", color: .orange) {
                                triggerPutProfile()
                            }
                            ActionButton(title: "DELETE Account", icon: "trash.fill", color: .red) {
                                triggerDeleteAccount()
                            }
                        }

                        // SECTION 2: Error Testing
                        CategorySection(title: "DIAGNOSTICS & FAULTS") {
                            ActionButton(title: "Trigger HTTP 404 (Not Found)", icon: "exclamationmark.triangle.fill", color: .pink) {
                                trigger404()
                            }
                            ActionButton(title: "Trigger HTTP 500 (Internal Server Error)", icon: "xmark.octagon.fill", color: .purple) {
                                trigger500()
                            }
                            ActionButton(title: "Simulate Connection Timeout", icon: "clock.fill", color: .gray) {
                                triggerTimeout()
                            }
                        }

                        // SECTION 3: Utility Capture
                        CategorySection(title: "SDK CONTROL ACTIONS") {
                            ActionButton(title: "Log Manual Mock Session Event", icon: "square.and.pencil", color: .indigo) {
                                triggerManualMockCapture()
                            }
                            ActionButton(title: "Clear Inspection Database Logs", icon: "clear.fill", color: .black) {
                                Tracea.shared.clear()
                                logOutput = "Database logs successfully cleared."
                            }
                        }
                    }
                    .padding()
                }

                // Console Logging Frame
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Text("CONSOLE RESPONSE LOGGER")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.secondary)
                        Spacer()
                        if isLoading {
                            ProgressView()
                                .scaleEffect(0.8)
                        }
                    }
                    ScrollView {
                        Text(logOutput)
                            .font(.system(.footnote, design: .monospaced))
                            .foregroundColor(.primary)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    .padding(10)
                    .frame(height: 140)
                    .background(Color(.systemGray6))
                    .cornerRadius(8)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(.systemGray4), lineWidth: 1)
                    )
                }
                .padding()
                .background(Color(.systemBackground))
                .shadow(color: Color.black.opacity(0.05), radius: 5, y: -2)
            }
            .navigationBarHidden(true)
        }
    }

    // API triggers
    private func triggerGetUsers() {
        isLoading = true
        NetworkManager.shared.performRequest(method: "GET", urlString: "https://jsonplaceholder.typicode.com/users") { res in
            isLoading = false
            logOutput = res
        }
    }

    private func triggerPostLogin() {
        isLoading = true
        let body = "{\"email\":\"test@company.com\",\"password\":\"super_secure_pass123\"}"
        let headers = ["Content-Type": "application/json"]
        NetworkManager.shared.performRequest(method: "POST", urlString: "https://httpbin.org/post", body: body, headers: headers) { res in
            isLoading = false
            logOutput = res
        }
    }

    private func triggerPutProfile() {
        isLoading = true
        let body = "{\"name\":\"Hari\",\"role\":\"Senior Architect\"}"
        let headers = ["Content-Type": "application/json"]
        NetworkManager.shared.performRequest(method: "PUT", urlString: "https://jsonplaceholder.typicode.com/posts/1", body: body, headers: headers) { res in
            isLoading = false
            logOutput = res
        }
    }

    private func triggerDeleteAccount() {
        isLoading = true
        NetworkManager.shared.performRequest(method: "DELETE", urlString: "https://jsonplaceholder.typicode.com/posts/1") { res in
            isLoading = false
            logOutput = res
        }
    }

    private func trigger404() {
        isLoading = true
        NetworkManager.shared.performRequest(method: "GET", urlString: "https://httpbin.org/status/404") { res in
            isLoading = false
            logOutput = res
        }
    }

    private func trigger500() {
        isLoading = true
        NetworkManager.shared.performRequest(method: "GET", urlString: "https://httpbin.org/status/500") { res in
            isLoading = false
            logOutput = res
        }
    }

    private func triggerTimeout() {
        isLoading = true
        NetworkManager.shared.performRequest(method: "GET", urlString: "https://httpbin.org/delay/15") { res in
            isLoading = false
            logOutput = res
        }
    }

    private func triggerManualMockCapture() {
        isLoading = true
        let call = Tracea.shared.startRequest(method: "POST", url: "https://api.tracea.internal/v1/mock-activation")
        call.requestHeaders(headers: ["X-Client-Platform": "iOS-Native"])
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            call.response(
                statusCode: 200,
                headers: ["Content-Type": "application/json", "X-Server-Source": "TraceaMockEngine"],
                body: "{\"status\": \"active\", \"mocked\": true, \"payload_id\": \"98a3e-ff01\"}",
                contentType: "application/json"
            )
            isLoading = false
            logOutput = "Logged manual mock activation call directly to Tracea sqlite storage."
        }
    }
}

// Custom UI Components
struct CategorySection<Content: View>: View {
    let title: String
    let content: Content

    init(title: String, @ViewBuilder content: () -> Content) {
        self.title = title
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(title)
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(.secondary)
                .padding(.leading, 4)
            VStack(spacing: 8) {
                content
            }
        }
    }
}

struct ActionButton: View {
    let title: String
    let icon: String
    let color: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Image(systemName: icon)
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundColor(color)
                    .frame(width: 24)
                Text(title)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(.primary)
                Spacer()
                Image(systemName: "chevron.right")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(.secondary.opacity(0.5))
            }
            .padding()
            .background(Color(.secondarySystemGroupedBackground))
            .cornerRadius(10)
            .shadow(color: Color.black.opacity(0.03), radius: 3, x: 0, y: 1)
        }
        .buttonStyle(PlainButtonStyle())
    }
}
