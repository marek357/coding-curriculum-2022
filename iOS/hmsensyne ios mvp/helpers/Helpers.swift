//
//  Helpers.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 13/02/2022.
//

import Foundation
import UIKit
import SwiftUI

// https://sarunw.com/posts/getting-number-of-days-between-two-dates/
extension Calendar {
    func numberOfDaysBetween(from: Date, to: Date) -> Int {
        let fromDate = startOfDay(for: from) // <1>
        let toDate = startOfDay(for: to) // <2>
        let numberOfDays = dateComponents([.day], from: fromDate, to: toDate) // <3>
        
        return numberOfDays.day!
    }
}

func stringToDate(timestamp: String) -> Date {
    let dateFormatter = DateFormatter()
    let timestamp = timestamp.components(separatedBy: ".")[0]
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
    return dateFormatter.date(from: timestamp) ?? Date.now
}

func getCurrentDateString() -> String {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
    return dateFormatter.string(from: Date.now)
}

class InferenceState: ObservableObject {
    @Published var runInference: Bool
    @Published var inferenceText: String = ""
    @ObservedObject var measurementManager: MeasurementManager = getMeasurementManager()
    
    init(runInference: Bool) {
        self.runInference = runInference
    }
    
    func stopInference() {
        DispatchQueue.main.async {
            self.runInference = false
        }
    }
    
    func startInference() {
        DispatchQueue.main.async {
            self.runInference = true
        }
    }
    
    func setInferenceText(text: String) {
        DispatchQueue.main.async {
            self.inferenceText = text
        }
    }
    
    func clearInferenceText() {
        self.setInferenceText(text: "")
    }
    
    func handleFinalInferenceResult(measurementStore: MeasurementStore) {
        if measurementManager.finalValueMeasurement != 0 {
            DispatchQueue.main.async {
                measurementStore.createMeasurement(heartRate: self.measurementManager.finalValueMeasurement)
                self.runInference = false
            }
        }
    }
}
