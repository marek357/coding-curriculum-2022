//
//  AuthorizationManager.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 28/01/2022.
//

import Foundation
import Firebase

class AuthorizationManager : ObservableObject {
    @Published var isSignedIn = Firebase.Auth.auth().currentUser != nil
    @Published var userCompletedOnboarding: Bool = false
    @Published var didHitBackend: Bool = false
    let userApi: UserApi = UserApi()
    
    init() {
        if self.isSignedIn {
            self.getUserCompletedOnboarding() {_, error in
                // assign value only after the onboarding completion result is fetched
                // otherwise the app might show the wrong screen before showing
                // the correct screen
                guard error == nil else {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) {
                        self.didHitBackend = true // fake delay to show splash screen
                    }
                    self.isSignedIn = false // error connecting to API
                    return
                }
                self.isSignedIn = true
            }
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) {
            self.didHitBackend = true // fake delay to show splash screen
        }
    }

    func signIn(email: String, password: String, completion:@escaping (Error?) -> ()) {
        Firebase.Auth.auth().signIn(withEmail: email, password: password) { (result, error) in
            guard error == nil else {
                completion(error)
                return
            }
            self.getUserCompletedOnboarding() {_, error in
                // assign value only after the onboarding completion result is fetched
                // otherwise the app might show the wrong screen before showing
                // the correct screen
                guard error == nil else {
                    completion(error)
                    return
                }
                self.isSignedIn = true
            }
        }
    }
    
    func signUp(name: String, age: String, email: String, password: String, completion:@escaping (Error?) -> ()) {
        Auth.auth().createUser(withEmail: email, password: password) { authResult, error in
            guard error == nil else {
                // error with creating the account
                completion(error)
                return
            }

            // account created, try to sign in
            let userData = UserData(user_completed_onboarding: false, name: name, age: Int(age) ?? 0)
            self.userApi.createUserData(data: userData) {_, error in
                guard error == nil else {
                    completion(error)
                    return
                }
            }
            self.signIn(email: email, password: password, completion: completion)
        }
    }
    
    func signOut() -> Bool {
        do {
            try Firebase.Auth.auth().signOut()
        } catch {
            return false
        }
        self.isSignedIn = false
        return true
    }
    
    func getUserCompletedOnboarding(completion:@escaping (Any?, Error?) -> ()) {
        self.userApi.getUserData() {result, error in
            guard error == nil else {
                completion(nil, error)
                return
            }
            if let userData = result as? UserData {
                self.userCompletedOnboarding = userData.user_completed_onboarding
                completion(userData.user_completed_onboarding, nil)
            } else {
                // there was an error
                completion(nil, error)
            }
        }
    }
    
    func deleteAccount() {
        self.userApi.deleteUserAccount() {error in
            if let error = error {
                print(error) // low level API error
            }
        }
    }
}
