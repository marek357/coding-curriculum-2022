//
//  MeasurementStore.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 13/02/2022.
//

import SwiftUI

struct MeasurementData: Encodable, Decodable, Identifiable {
    var id: Int
    let heart_rate_value: Double?
    let blood_pressure_systolic_value: Float?
    let blood_pressure_diastolic_value: Float?
    let timestamp: String
}

struct MeasurementDataCreate: Encodable, Decodable {
    let heart_rate_value: Double?
    let blood_pressure_systolic_value: Float?
    let blood_pressure_diastolic_value: Float?
    let timestamp: String
}
