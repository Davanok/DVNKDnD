import SwiftUI

@main
struct iOSApp: App {
    init() {
        MainKt.doInitKoin()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}