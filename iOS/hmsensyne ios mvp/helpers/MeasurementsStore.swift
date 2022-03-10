//
//  MeasurementsStore.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 19/02/2022.
//

import Foundation
import Combine

// https://stackoverflow.com/questions/56496359/swiftui-view-viewdidload
class MeasurementStore: ObservableObject {
    var didChange = PassthroughSubject<MeasurementStore, Never>()
    let api: MeasurementsApi
    var measurementManager: MeasurementManager = getMeasurementManager()
    @Published private(set) var measurements: [MeasurementData] = [] {
        didSet {
            didChange.send(self)
        }
    }
    
    init() {
        self.api = MeasurementsApi()
    }
    
    func fetch(startDate: Date?, endDate: Date?) {
        self.api.getMeasurementHistory(startDate: startDate, endDate: endDate) { [weak self] result in
            DispatchQueue.main.async {
                switch result {
                case .success(let measurements):
                    self?.measurements = measurements
                case .failure:
                    self?.measurements = []
                }
            }
        }
    }
    
    func deleteMeasurement(idx: Int) {
        self.api.deleteMeasurement(measurementID: self.measurements[idx].id) {result in
            DispatchQueue.main.async {
                switch result {
                case .success:
                    self.fetch(startDate: nil, endDate: nil)
                case .failure:
                    // present error
                    self.fetch(startDate: nil, endDate: nil)
                }
            }
        }
    }
    
    func createMeasurement(heartRate: Double) {
        self.api.createMeasurement(
            data: MeasurementDataCreate(
                heart_rate_value: Double(round(100 * self.measurementManager.finalValueMeasurement) / 100),
                blood_pressure_systolic_value: nil,
                blood_pressure_diastolic_value: nil,
                timestamp: getCurrentDateString()
            )
        ) {result in
            DispatchQueue.main.async {
                switch result {
                case .success:
                    self.fetch(startDate: nil, endDate: nil)
                case .failure:
                    // present error
                    self.fetch(startDate: nil, endDate: nil)
                }
            }
        }
    }
    
    func getMeasurementData() -> [Double] {
        var hr_data: [Double] = []
        for measurement in self.measurements {
            hr_data.append(measurement.heart_rate_value ?? -1)
        }
        return hr_data
    }
}
