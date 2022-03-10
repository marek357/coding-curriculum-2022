//
//  Home.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 08/02/2022.
//

import Foundation
import SwiftUI
import AVFoundation


struct HomeView: View {
    @ObservedObject var authorizationManager : AuthorizationManager
    let measurementStore: MeasurementStore = MeasurementStore()
    var body: some View {
        TabView {
            MeasurementView(authorizationManager: authorizationManager)
                .environmentObject(measurementStore)
                .tabItem {
                    Label("Measurements", systemImage: "waveform.path.ecg.rectangle.fill")
                        .environment(\.symbolVariants, .none)
                        .accentColor(Color(red: 173 / 255, green: 216 / 255, blue: 230 / 255))
                }
            HistoryView(authorizationManager: authorizationManager)
                .environmentObject(measurementStore)
                .tabItem {
                    Label("History", systemImage: "doc.text.below.ecg.fill")
                        .environment(\.symbolVariants, .none)

                }
            SettingsView(authorizationManager: authorizationManager)
                .tabItem {
                    Label("Settings", systemImage: "gear")
                        .environment(\.symbolVariants, .none)
                }
        }
    }
}

