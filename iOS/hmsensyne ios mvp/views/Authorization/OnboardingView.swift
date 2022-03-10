//
//  Onboarding.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 21/01/2022.
//

import Foundation
import SwiftUI
import Firebase

struct OnboardingPageView: View {
    let title: String
    let description: String
    let imageName: String
    let fontColour: Color
    
    var body: some View {
        VStack {
            Image(imageName)
                .resizable()
                .scaledToFit()
                .padding(50)
            Text(title)
                .font(.system(size: 27, weight: .heavy, design: .default))
                .padding(10)
                .foregroundColor(fontColour)
            Text(description)
                .foregroundColor(fontColour)
        }.frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

struct MeasurementDemoPreviewView: View {
    @State var measurementButtonPressed: Bool = false
    @State var faceFound: Bool = false
    @State var measurementWorks: Bool = false
    @State var authorizationManager : AuthorizationManager
    @State var presentErrorAlert: Bool = false

    let userApi: UserApi = UserApi()
    
    func getFrameColour() -> Color {
        if !measurementButtonPressed {
            return .blue
        }
        if !faceFound {
            return .red
        }
        if !measurementWorks {
            return .yellow
        }
        return .green
    }
    
    func getInfoText() -> String {
        if !self.measurementButtonPressed {
            return "No measurement is being taken"
        }
        if !self.faceFound {
            return "Face not found"
        }
        if !self.measurementWorks {
            return "Face found \nAlgorithm needs a moment to start"
        }
        return "Face found \nMeasurement is being taken"
    }
    
    var body: some View {
        VStack {
            Text(getInfoText())
                .font(.system(size: 20, weight: .regular, design: .default))
                .multilineTextAlignment(.center)
                .padding(10)
            Image("person")
                .resizable()
                .scaledToFit()
                .frame(height: 400)
                .overlay(
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(getFrameColour(), lineWidth: 4)
                        .padding()
                )
            Button(action: {
                self.faceFound = false
                self.measurementWorks = false
                self.measurementButtonPressed = !self.measurementButtonPressed
            }) {
                VStack {
                    Image(systemName: "waveform.path.ecg")
                        .font(.system(size: 40, weight: .heavy, design: .default))
                        .foregroundColor(self.measurementButtonPressed ? .green : .blue)
                }.padding(10.0)
                    .overlay(
                        Circle()
                            .stroke(lineWidth: 2.0)
                            .foregroundColor(self.measurementButtonPressed ? .green : .blue)
                    )
            }.padding(.bottom, 20.0)
            if self.measurementWorks {
                Button("Take me to the app!", action: {
                    DispatchQueue.main.async {
                        // user completed onboarding
                        userApi.updateUserData(data: UserDataUpdate(user_completed_onboarding: true)) {(data, error) in
                            guard error == nil else {
                                self.presentErrorAlert = true
                                return
                            }
                            self.authorizationManager.getUserCompletedOnboarding() {_, error in
                                // query results are handled in authorization manager
                                guard error == nil else {
                                    self.presentErrorAlert = true
                                    return
                                }
                            }
                        }
                    }
                })
                    .buttonStyle(.bordered)
                    .padding(.top, 20)
            }
            
            if !self.measurementButtonPressed {
                Text("Press button to start measurement")
                    .font(.system(size: 20, weight: .regular, design: .default))
                    .padding(10)
            }
            
            if self.measurementButtonPressed && !self.measurementWorks {
                self.faceFound ?
                Button("Simulate measurement start", action: { self.measurementWorks = true })
                : Button("Simulate face found", action: { self.faceFound = true })
            }
        }.alert(
            "Server not responding",
            isPresented: $presentErrorAlert,
            actions: {},
            message: { Text("Unfortunately remote server is not responding.\nPlease try again later") }
        ).accentColor(.white)
        .padding()
    }
}

struct OnboardingView: View {
    @ObservedObject var authorizationManager : AuthorizationManager
    static let onboardingFontColour: Color = Color(red: 58 / 255, green: 55 / 255, blue: 54 / 255)
        
    var body: some View {
        TabView {
            OnboardingPageView(
                title: "Measure vitals anywhere",
                description: "Travel without any additional equipment",
                imageName: "ship",
                fontColour: OnboardingView.onboardingFontColour
            ).background(
                Color(red: 210 / 255, green: 250 / 255, blue: 255 / 255)
            )
            OnboardingPageView(
                title: "AI powered measurements",
                description: "Get measurements from AI-based technology",
                imageName: "ai",
                fontColour: OnboardingView.onboardingFontColour
            ).background(
                Color(red: 250 / 255, green: 231 / 255, blue: 145 / 255)
            )
            OnboardingPageView(
                title: "Fast measurements",
                description: "Get reliable measurements in up to 15 seconds",
                imageName: "watch",
                fontColour: Color.white
            ).background(
                Color(red: 65 / 255, green: 62 / 255, blue: 81 / 255)
            )
            OnboardingPageView(
                title: "Check your vitals on the go",
                description: "Become the best version of yourself",
                imageName: "gym",
                fontColour: OnboardingView.onboardingFontColour
            ).background(Color.white)
            MeasurementDemoPreviewView(
                authorizationManager: authorizationManager
            )
        }
        .tabViewStyle(PageTabViewStyle())
        .edgesIgnoringSafeArea(.vertical)
        .onAppear(perform: {
            UIScrollView.appearance().bounces = false
        })
    }
}

