//
//  heartbeat.swift
//  hmssensyne
//
//  Created by Marek Masiak on 17/11/2021.
//

import SwiftUI
import UIKit
import AVFoundation

let downsampleRate = 1
let engine = OpenCVWrapper()
let manager = MeasurementManager()
var initialised = false

func handleFrame(frame: UIImage, frameNumber: Int) {
    if !initialised {
        engine.initialise()
        initialised = true
    }
    
    if frameNumber % downsampleRate == 0 {
        engine.getHR(frame)
        manager.resultLogicHandler()
    }
}

func destroyEngine() {
    initialised = false
    manager.restartMeasurement()
}

func getInferenceTextFromEngine() -> String {
    if !initialised {
        return ""
    }
    return engine.getInferenceTextFromEngine()
}

func getHeartRateFromEngine() -> Double {
    if !initialised {
        return -1.0
    }
    return engine.getHeartRateFromEngine()
}

func getFaceDetectionStatusColor(runInference: Bool) -> Color? {
    if !runInference {
        return nil
    }
    switch getHeartRateFromEngine() {
    case let hr where hr > 0:
        return Color.green
    case let hr where hr == 0:
        return Color.yellow
    default:
        return Color.red
    }
}

func getMeasurementManager() -> MeasurementManager {
    return manager
}

class MeasurementManager: ObservableObject {
    @Published var finalValueMeasurement: Double = 0.0
    @Published var showSuccessPopup: Bool = false
    
    var userApi = UserApi()
    var readings: [Double] = []
    let MINIMAL_NUM_OF_READINGS: Int = 25
    let EPS: Double = 2.0
    
    func resultLogicHandler() {
        let reading = getHeartRateFromEngine()
        switch reading {
        case let hr where hr > 50:
            guard self.readings.count > 0 else {
                self.readings.append(hr)
                return
            }
            guard self.readings.count > 1 else {
                // second measurement has to be handled with its own algorithm
                // as the standard deviation is 0 and ony a measurement that is
                // exactly the same will be added to the measurements list
                if fabs((self.readings.last ?? 0.0) - hr) < EPS {
                    self.readings.append(hr)
                } else {
                    self.readings = [hr]
                }
                return
            }
            let arithmeticMean = self.readings.reduce(0.0, +) / Double(self.readings.count)
            let stdDeviationArr = self.readings.map { pow($0 - arithmeticMean, 2.0) }
            let variance = stdDeviationArr.reduce(0.0, +)
            let stdDeviation = sqrt(variance)
            if fabs(arithmeticMean - hr) < stdDeviation {
                self.readings.append(hr)
            } else {
                self.readings = [hr]
            }
            break
        default:
            self.readings = []
        }
        
        if self.readings.count >= MINIMAL_NUM_OF_READINGS {
            DispatchQueue.main.async {
                self.finalValueMeasurement = self.readings.reduce(0, +) / Double(self.readings.count)
                self.showSuccessPopup = true
            }
        }
    }
    
    func restartMeasurement() {
        self.readings = []
    }
    
}
