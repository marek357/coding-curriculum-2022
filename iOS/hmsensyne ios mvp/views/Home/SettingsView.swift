//
//  Settings.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 12/02/2022.
//

import Foundation
import SwiftUI

struct SettingsView: View {
    @ObservedObject var authorizationManager : AuthorizationManager
    var body: some View {
        VStack {
            NavigationView{
                List {
                    Section(header: Text("User settings")) {
                        Button(action: {
                            var _ = self.authorizationManager.deleteAccount()
                        }) {
                            HStack {
                                Spacer()
                                Text("Delete account and data")
                                    .foregroundColor(Color.red)
                                Spacer()
                            }
                        }
                        Button(action: {
                            var _ = self.authorizationManager.signOut()
                        }) {
                            HStack {
                                Spacer()
                                Text("Sign out")
                                    .foregroundColor(Color.red)
                                Spacer()
                            }
                        }
                    }
                    
                }
                .navigationTitle("Settings")
                .listStyle(SidebarListStyle())
            }
        }
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
        .cornerRadius(15)
        .padding()
    }
}
