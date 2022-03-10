//
//  ContentView.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 21/01/2022.
//

import SwiftUI
import Firebase

struct ContentView: View {
    @StateObject var authorizationManager = AuthorizationManager()
    
    var body: some View {
        if !authorizationManager.didHitBackend {
            SplashScreen()
        } else {
            if authorizationManager.isSignedIn {
                if authorizationManager.userCompletedOnboarding {
                    HomeView(authorizationManager: authorizationManager)
                } else {
                    OnboardingView(authorizationManager: authorizationManager)
                }
            } else {
                SignInView(authorizationManager: authorizationManager)
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
