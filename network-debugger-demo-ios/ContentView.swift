import SwiftUI

struct ContentView: View {
    @StateObject private var service = NetworkDemoService()
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(red: 0.07, green: 0.07, blue: 0.07)
                    .ignoresSafeArea()
                
                VStack(spacing: 16) {
                    // Header Card
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Network Debugger iOS Demo")
                            .font(.title2)
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                        
                        Text("Tap buttons below to execute URLSession API calls and inspect them in real-time.")
                            .font(.subheadline)
                            .foregroundColor(.gray)
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(red: 0.12, green: 0.12, blue: 0.18))
                    .cornerRadius(12)
                    .padding(.horizontal)
                    
                    // Trigger Buttons
                    ScrollView {
                        VStack(spacing: 12) {
                            Button(action: { service.executeGet() }) {
                                Label("Execute GET Request", systemImage: "arrow.down.circle.fill")
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(Color.green.opacity(0.2))
                                    .foregroundColor(.green)
                                    .cornerRadius(10)
                            }
                            
                            Button(action: { service.executePost() }) {
                                Label("Execute POST (Redacted Payload)", systemImage: "arrow.up.circle.fill")
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(Color.blue.opacity(0.2))
                                    .foregroundColor(.blue)
                                    .cornerRadius(10)
                            }
                            
                            Button(action: { service.executeError404() }) {
                                Label("Trigger 404 Client Error", systemImage: "exclamationmark.triangle.fill")
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(Color.orange.opacity(0.2))
                                    .foregroundColor(.orange)
                                    .cornerRadius(10)
                            }
                            
                            Button(action: { service.executeError500() }) {
                                Label("Trigger 500 Server Error", systemImage: "xmark.octagon.fill")
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(Color.red.opacity(0.2))
                                    .foregroundColor(.red)
                                    .cornerRadius(10)
                            }
                        }
                        .padding(.horizontal)
                    }
                    
                    // Console Logs Output
                    VStack(alignment: .leading, spacing: 8) {
                        Text("API Activity Log:")
                            .font(.caption)
                            .fontWeight(.semibold)
                            .foregroundColor(.gray)
                        
                        ScrollView {
                            LazyVStack(alignment: .leading, spacing: 6) {
                                ForEach(service.logs, id: \.self) { log in
                                    Text(log)
                                        .font(.system(.caption, design: .monospaced))
                                        .foregroundColor(Color(red: 0.8, green: 0.8, blue: 0.9))
                                }
                            }
                        }
                        .frame(maxHeight: 180)
                    }
                    .padding()
                    .background(Color(red: 0.1, green: 0.1, blue: 0.14))
                    .cornerRadius(12)
                    .padding(.horizontal)
                }
                .padding(.vertical)
            }
            .navigationTitle("Network Inspector")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
