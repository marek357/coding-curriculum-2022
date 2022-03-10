//
//  SignIn.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 22/01/2022.
//

import Foundation
import SwiftUI


struct SignInView: View {
    @State var email: String = ""
    @State var password: String = ""
    @ObservedObject var authorizationManager : AuthorizationManager
    @State private var presentLoginErrorAlert = false
    @State private var errorMessage: String = ""
    
    let lightGreyColor: Color = Color(red: 245 / 255, green: 245 / 255, blue: 245 / 255)
    let backgroundColor: Color = Color(red: 22 / 255, green: 37 / 255, blue: 71 / 255)
    let actionColor: Color = Color(red: 226 / 255, green: 63 / 255, blue: 90 / 255)
    let horizontalPadding: CGFloat = 40.0
    
    var body: some View {
        NavigationView {
            VStack {
                Spacer()
                TextField("Email", text: $email)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
                    .textContentType(.emailAddress)
                    .padding(.horizontal, horizontalPadding)
                    .background(backgroundColor)
                    .cornerRadius(15.0)
                    .overlay(
                        RoundedRectangle(cornerRadius: 25)
                            .stroke(Color.white, lineWidth: 2)
                            .padding(.horizontal, 20.0)
                            .frame(height: 55)
                    )
                    .font(.system(size: 20, design: .default))
                    .foregroundColor(Color.white)
                    .padding(.bottom, 50)
                SecureField("Password", text: $password)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
                    .padding(.horizontal, horizontalPadding)
                    .background(backgroundColor)
                    .cornerRadius(15.0)
                    .overlay(
                        RoundedRectangle(cornerRadius: 25)
                            .stroke(Color.white, lineWidth: 2)
                            .padding(.horizontal, 20.0)
                            .frame(height: 55)
                    )
                    .font(.system(size: 20, design: .default))
                    .foregroundColor(Color.white)
                    .padding(.bottom, 50)
                Button(action: {
                    authorizationManager.signIn(
                        email: email, password: password
                    ){(error) in
                        guard error == nil else {
                            self.errorMessage = error?.localizedDescription ?? "Provided credentials are incorrect"
                            self.presentLoginErrorAlert = true
                            return
                        }
                    }
                }) {
                    Text("Sign In")
                        .frame(minWidth: 0, maxWidth: .infinity)
                        .font(.system(size: 18))
                        .padding()
                        .foregroundColor(.white)
                }
                .background(actionColor)
                .cornerRadius(25)
                .padding(.horizontal, 20)
                Spacer()
                NavigationLink(destination: SignUpView(authorizationManager: authorizationManager)) {
                    Text("Don't have an account? Sign Up")
                        .frame(minWidth: 0, maxWidth: .infinity)
                        .font(.system(size: 18))
                        .padding()
                        .foregroundColor(.white)
                }
                .foregroundColor(Color.white)
                .padding(.bottom, 30)
            }
            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
            .background(backgroundColor)
            .preferredColorScheme(.dark)
        }.alert(
            "Unable to log in",
            isPresented: $presentLoginErrorAlert,
            actions: {},
            message: { Text(self.errorMessage) }
        ).accentColor(.white)
    }
}
