import SwiftUI

struct ContentView: View {
    @StateObject private var service = NetworkDemoService()
    @State private var selectedEvent: CapturedNetworkEvent? = nil
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(red: 0.07, green: 0.07, blue: 0.09)
                    .ignoresSafeArea()
                
                VStack(spacing: 16) {
                    // Header Banner
                    VStack(alignment: .leading, spacing: 6) {
                        HStack {
                            Text("Network Debugger iOS")
                                .font(.title3)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                            Spacer()
                            BadgeView(text: "\(service.capturedEvents.count) Calls", color: .purple)
                        }
                        
                        Text("KMP Multiplatform Engine & URLSession Interception Demo")
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                    .padding()
                    .background(Color(red: 0.12, green: 0.12, blue: 0.18))
                    .cornerRadius(12)
                    .padding(.horizontal)
                    
                    // Main Action Buttons
                    VStack(spacing: 10) {
                        Button(action: {
                            service.isDebuggerSheetPresented = true
                        }) {
                            HStack {
                                Image(systemName: "globe")
                                Text("Open Network Debugger UI (\(service.capturedEvents.count))")
                                    .fontWeight(.bold)
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color(red: 0.48, green: 0.43, blue: 0.96))
                            .foregroundColor(.white)
                            .cornerRadius(12)
                            .shadow(color: Color.purple.opacity(0.4), radius: 8, x: 0, y: 4)
                        }
                        
                        HStack(spacing: 10) {
                            Button(action: { service.executeGet() }) {
                                Label("GET Request", systemImage: "arrow.down.circle.fill")
                                    .font(.subheadline)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 12)
                                    .background(Color.green.opacity(0.15))
                                    .foregroundColor(.green)
                                    .cornerRadius(10)
                            }
                            
                            Button(action: { service.executePost() }) {
                                Label("POST (Redacted)", systemImage: "arrow.up.circle.fill")
                                    .font(.subheadline)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 12)
                                    .background(Color.blue.opacity(0.15))
                                    .foregroundColor(.blue)
                                    .cornerRadius(10)
                            }
                        }
                        
                        HStack(spacing: 10) {
                            Button(action: { service.executeError404() }) {
                                Label("404 Error", systemImage: "exclamationmark.triangle.fill")
                                    .font(.subheadline)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 12)
                                    .background(Color.orange.opacity(0.15))
                                    .foregroundColor(.orange)
                                    .cornerRadius(10)
                            }
                            
                            Button(action: { service.executeError500() }) {
                                Label("500 Server Error", systemImage: "xmark.octagon.fill")
                                    .font(.subheadline)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 12)
                                    .background(Color.red.opacity(0.15))
                                    .foregroundColor(.red)
                                    .cornerRadius(10)
                            }
                        }
                    }
                    .padding(.horizontal)
                    
                    // Real-Time Activity Log
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Text("Activity Log:")
                                .font(.caption)
                                .fontWeight(.semibold)
                                .foregroundColor(.gray)
                            Spacer()
                            Button("Clear") { service.clearEvents() }
                                .font(.caption)
                                .foregroundColor(.gray)
                        }
                        
                        ScrollView {
                            LazyVStack(alignment: .leading, spacing: 6) {
                                ForEach(service.logs, id: \.self) { log in
                                    Text(log)
                                        .font(.system(.caption, design: .monospaced))
                                        .foregroundColor(Color(red: 0.8, green: 0.8, blue: 0.9))
                                }
                            }
                        }
                        .frame(maxHeight: 140)
                    }
                    .padding()
                    .background(Color(red: 0.1, green: 0.1, blue: 0.14))
                    .cornerRadius(12)
                    .padding(.horizontal)
                    
                    Spacer()
                }
                .padding(.vertical)
            }
            .navigationTitle("Network Debugger")
            .navigationBarTitleDisplayMode(.inline)
            .sheet(isPresented: $service.isDebuggerSheetPresented) {
                NetworkDebuggerInspectorSheet(events: service.capturedEvents)
            }
        }
    }
}

// Dark-Theme iOS Debugger Inspector Modal Sheet
struct NetworkDebuggerInspectorSheet: View {
    let events: [CapturedNetworkEvent]
    @Environment(\.dismiss) private var dismiss
    @State private var selectedEvent: CapturedNetworkEvent? = nil
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(red: 0.07, green: 0.07, blue: 0.09)
                    .ignoresSafeArea()
                
                if events.isEmpty {
                    VStack(spacing: 12) {
                        Image(systemName: "globe")
                            .font(.system(size: 48))
                            .foregroundColor(.gray)
                        Text("No Network Requests Captured Yet")
                            .font(.headline)
                            .foregroundColor(.gray)
                        Text("Execute GET, POST, or status calls to inspect them here.")
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                } else {
                    List {
                        ForEach(events) { event in
                            Button(action: { selectedEvent = event }) {
                                HStack(spacing: 12) {
                                    MethodBadgeView(method: event.method)
                                    
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text(event.url)
                                            .font(.system(.subheadline, design: .monospaced))
                                            .fontWeight(.semibold)
                                            .foregroundColor(.white)
                                            .lineLimit(1)
                                        
                                        HStack(spacing: 8) {
                                            Text(event.timestamp)
                                                .font(.caption2)
                                                .foregroundColor(.gray)
                                            Text("•")
                                                .font(.caption2)
                                                .foregroundColor(.gray)
                                            Text("\(event.durationMs) ms")
                                                .font(.caption2)
                                                .foregroundColor(.gray)
                                        }
                                    }
                                    
                                    Spacer()
                                    
                                    StatusBadgeView(statusCode: event.statusCode)
                                }
                                .padding(.vertical, 4)
                            }
                            .listRowBackground(Color(red: 0.12, green: 0.12, blue: 0.16))
                        }
                    }
                    .listStyle(.plain)
                }
            }
            .navigationTitle("Network Inspector (\(events.count))")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") { dismiss() }
                }
            }
            .sheet(item: $selectedEvent) { event in
                EventDetailView(event: event)
            }
        }
    }
}

// Request Detail View Modal
struct EventDetailView: View {
    let event: CapturedNetworkEvent
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(red: 0.07, green: 0.07, blue: 0.09)
                    .ignoresSafeArea()
                
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        // Summary Card
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                MethodBadgeView(method: event.method)
                                StatusBadgeView(statusCode: event.statusCode)
                                Spacer()
                                Text("\(event.durationMs) ms")
                                    .font(.caption)
                                    .foregroundColor(.gray)
                            }
                            Text(event.url)
                                .font(.system(.body, design: .monospaced))
                                .foregroundColor(.white)
                        }
                        .padding()
                        .background(Color(red: 0.12, green: 0.12, blue: 0.16))
                        .cornerRadius(12)
                        
                        // cURL Exporter
                        VStack(alignment: .leading, spacing: 6) {
                            Text("cURL Command")
                                .font(.caption)
                                .fontWeight(.bold)
                                .foregroundColor(Color(red: 0.48, green: 0.43, blue: 0.96))
                            Text(event.curlCommand)
                                .font(.system(.caption, design: .monospaced))
                                .padding()
                                .background(Color.black.opacity(0.4))
                                .foregroundColor(.green)
                                .cornerRadius(8)
                        }
                        
                        // Request Headers
                        if !event.requestHeaders.isEmpty {
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Request Headers")
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .foregroundColor(Color(red: 0.48, green: 0.43, blue: 0.96))
                                ForEach(Array(event.requestHeaders.keys), id: \.self) { key in
                                    HStack {
                                        Text(key).foregroundColor(.gray)
                                        Spacer()
                                        Text(event.requestHeaders[key] ?? "").foregroundColor(.white)
                                    }
                                    .font(.caption)
                                }
                            }
                            .padding()
                            .background(Color(red: 0.12, green: 0.12, blue: 0.16))
                            .cornerRadius(12)
                        }
                        
                        // Response Body
                        if let body = event.responseBody {
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Response Payload")
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .foregroundColor(Color(red: 0.48, green: 0.43, blue: 0.96))
                                Text(body)
                                    .font(.system(.caption, design: .monospaced))
                                    .foregroundColor(Color(red: 0.8, green: 0.8, blue: 0.9))
                                    .padding()
                                    .background(Color.black.opacity(0.4))
                                    .cornerRadius(8)
                            }
                        }
                    }
                    .padding()
                }
            }
            .navigationTitle("Details")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Close") { dismiss() }
                }
            }
        }
    }
}

// Helpers
struct MethodBadgeView: View {
    let method: String
    var body: some View {
        Text(method)
            .font(.caption2)
            .fontWeight(.bold)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(method == "GET" ? Color.green.opacity(0.2) : Color.blue.opacity(0.2))
            .foregroundColor(method == "GET" ? .green : .blue)
            .cornerRadius(6)
    }
}

struct StatusBadgeView: View {
    let statusCode: Int
    var body: some View {
        Text("\(statusCode)")
            .font(.caption2)
            .fontWeight(.bold)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(statusCode < 400 ? Color.green.opacity(0.2) : Color.red.opacity(0.2))
            .foregroundColor(statusCode < 400 ? .green : .red)
            .cornerRadius(6)
    }
}

struct BadgeView: View {
    let text: String
    let color: Color
    var body: some View {
        Text(text)
            .font(.caption2)
            .fontWeight(.semibold)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(color.opacity(0.2))
            .foregroundColor(color)
            .cornerRadius(6)
    }
}
