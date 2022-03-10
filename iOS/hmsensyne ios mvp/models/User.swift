//
//  User.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 07/02/2022.
//
import Foundation

struct UserData : Encodable, Decodable {
    var user_completed_onboarding: Bool
    var name: String
    var age: Int
}

struct UserDataUpdate : Encodable {
    // backend allows for partial update
    // which allows the fields to be optional
    var user_completed_onboarding: Bool?
    var name: String?
    var age: Int?
}
