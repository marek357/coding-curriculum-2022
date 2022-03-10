//
//  SignUp.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 24/01/2022.
//

import Foundation
import SwiftUI
import Firebase
import FirebaseFirestore

struct SignUpField: View {
    let placeholder: String
    let contentType: UITextContentType?
    let autocapitalization: UITextAutocapitalizationType
    @Binding var value: String
    
    var body: some View {
        TextField(placeholder, text: $value)
            .autocapitalization(autocapitalization)
            .disableAutocorrection(true)
            .textContentType(contentType)
            .padding(.horizontal, SignUpView.horizontalPadding)
            .background(SignUpView.backgroundColor)
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
    }
}

struct SignUpView: View {
    @State var email: String = ""
    @State var password: String = ""
    @State var name: String = ""
    @State var age: String = ""
    @State private var presentSignUpErrorAlert = false
    @State private var errorMessage: String = ""
    @ObservedObject var authorizationManager : AuthorizationManager
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    static let backgroundColor: Color = Color(red: 254 / 255, green: 62 / 255, blue: 75 / 255)
    static let horizontalPadding: CGFloat = 40.0
    
    var body: some View {
        VStack {
            Image("robot")
                .resizable()
                .scaledToFit()
                .frame(width: 200, height: 200)
            SignUpField(
                placeholder: "Name",
                contentType: .name,
                autocapitalization: .words,
                value: $name
            )
            SignUpField(
                placeholder: "Age",
                contentType: .name,
                autocapitalization: .none,
                value: $age
            )
            SignUpField(
                placeholder: "Email",
                contentType: nil,
                autocapitalization: .none,
                value: $email
            )
            SecureField("Password", text: $password)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                .padding(.horizontal, SignUpView.horizontalPadding)
                .background(SignUpView.backgroundColor)
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
                authorizationManager.signUp(
                    name: name, age: age,
                    email: email, password: password
                ) {(error) in
                    guard error == nil else {
                        self.errorMessage = error?.localizedDescription ?? "Unable to sign up"
                        self.presentSignUpErrorAlert = true
                        return
                    }
                }
            }) {
                Text("Sign Up")
                    .frame(minWidth: 0, maxWidth: .infinity)
                    .font(.system(size: 18))
                    .padding()
                    .foregroundColor(.black)
            }
            .background(Color.white)
            .cornerRadius(25)
            .padding(.horizontal, 20)
            Spacer()
        }
        .background(Color(red: 254 / 255, green: 62 / 255, blue: 75 / 255))
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
        .alert(
            "Unable to sign up",
            isPresented: $presentSignUpErrorAlert,
            actions: {},
            message: { Text(self.errorMessage) }
        )
    }
}
