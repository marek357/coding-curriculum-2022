//
//  UserApi.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 07/02/2022.
//

import Foundation
import Alamofire
import Firebase

class UserApi: ObservableObject {
    let backendURL = Bundle.main.object(forInfoDictionaryKey: "BACKEND_URL") as! String
    
    func createUserData(data: UserData, completion:@escaping (UserData?, Error?) -> ())  {
        if let jsonData = try? JSONEncoder().encode(data) {
            let userDataArray = try! JSONSerialization.jsonObject(with: jsonData, options: [])
            Firebase.Auth.auth().currentUser?.getIDToken() { (idToken, error) in
                guard error == nil else {
                    completion(nil, error)
                    return
                }
                if let token = idToken {
                    let headers: HTTPHeaders = [
                        "Authorization": "Bearer \(token)",
                        "Content-Type": "application/json"
                    ]
                    
                    AF.request("\(self.backendURL)/user", method: .post, parameters: (userDataArray as! Parameters), encoding: JSONEncoding.default, headers: headers)
                        .responseDecodable(of: UserData.self) { response in
                            switch response.result {
                            case .success(let userData):
                                completion(userData, nil)
                            case .failure(let error):
                                completion(nil, error)
                            }
                        }
                }
            }
        }
    }
    
    func updateUserData(data: UserDataUpdate, completion:@escaping (Any?, Error?) -> ()) {
        if let jsonData = try? JSONEncoder().encode(data) {
            let userDataArray = try! JSONSerialization.jsonObject(with: jsonData, options: [])
            Firebase.Auth.auth().currentUser?.getIDToken() { (idToken, error) in
                guard error == nil else {
                    completion(nil, error)
                    return
                }
                if let token = idToken {
                    let headers: HTTPHeaders = [
                        "Authorization": "Bearer \(token)",
                        "Content-Type": "application/json"
                    ]
                    AF.request("\(self.backendURL)/user", method: .patch, parameters: (userDataArray as! Parameters), encoding: JSONEncoding.default, headers: headers)
                        .responseDecodable(of: UserData.self) { response in
                            switch response.result {
                            case .success(let userData):
                                completion(userData, nil)
                            case .failure(let error):
                                completion(nil, error)
                            }
                        }
                }
            }
        }
    }
    
    func getUserData(completion:@escaping (Any?, Error?) -> ()) {
        Firebase.Auth.auth().currentUser?.getIDToken() { (idToken, error) in
            guard error == nil else {
                completion(nil, error)
                return
            }
            if let token = idToken {
                let headers: HTTPHeaders = [
                    "Authorization": "Bearer \(token)",
                    "Content-Type": "application/json"
                ]
                
                AF.request("\(self.backendURL)/user", method: .get, headers: headers)
                    .responseDecodable(of: UserData.self) { response in
                        switch response.result {
                        case .success(let userData):
                            completion(userData, nil)
                        case .failure(let error):
                            completion(nil, error)
                        }
                    }
            }
        }
    }
    
    func deleteUserAccount(completion:@escaping (Error?) -> ()) {
        Firebase.Auth.auth().currentUser?.getIDToken() { (idToken, error) in
            guard error == nil else {
                completion(error)
                return
            }
            if let token = idToken {
                let headers: HTTPHeaders = [
                    "Authorization": "Bearer \(token)",
                    "Content-Type": "application/json"
                ]
                
                AF.request("\(self.backendURL)/user", method: .delete, headers: headers)
                    .response { response in
                        switch response.result {
                        case .success:
                            let user = Auth.auth().currentUser
                            user?.delete { error in
                                if let error = error {
                                    completion(error)
                                }
                            }
                            completion(nil)
                        case .failure(let error):
                            completion(error)
                        }
                    }
            }
        }
        
    }
}
