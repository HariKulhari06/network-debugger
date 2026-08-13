import SwiftUI
import Tracea

@main
struct TraceaDemoApp: App {
    init() {
        // Initialize the Tracea SDK
        // Since we are on iOS, context is null. Under the hood, Tracea uses NSHomeDirectory to store sqlite databases.
        Tracea.shared.initialize(context: nil, config: TraceaConfig(enabled: true))
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
