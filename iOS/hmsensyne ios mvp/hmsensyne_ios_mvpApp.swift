//
//  hmsensyne_ios_mvpApp.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 21/01/2022.
//

import SwiftUI
import Firebase

@main
struct hmsensyne_ios_mvpApp: App {
    init() {
        FirebaseApp.configure()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
