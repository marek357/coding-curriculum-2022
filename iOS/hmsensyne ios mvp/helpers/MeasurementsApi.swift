//
//  MeasurementsApi.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 19/02/2022.
//

import Foundation
import Alamofire
import Firebase

class MeasurementsApi: ObservableObject {
    let backendURL = Bundle.main.object(forInfoDictionaryKey: "BACKEND_URL") as! String

    func getMeasurementHistory(startDate: Date?, endDate: Date?, completion:@escaping (Result<[MeasurementData], AFError>) -> ()) {
        Firebase.Auth.auth().currentUser?.getIDToken() { (idToken, error) in
            guard error == nil else {
//                completion(Result(catching: {
//                    throw (error! as AFError) // this will never be nil
//                }))
                return
            }
            if let token = idToken {
                let headers: HTTPHeaders = [
                    "Authorization": "Bearer \(token)",
                    "Content-Type": "application/json"
                ]
                var parameters: Dictionary<String, Date> = [:]
                if let startDate = startDate {
                    parameters["start_date"] = startDate
                }
                
                if let endDate = endDate {
                    parameters["end_date"] = endDate
                }

                
                AF.request("\(self.backendURL)/measurements", method: .get, parameters: parameters, headers: headers)
                    .responseDecodable(of: [MeasurementData].self) { response in
                        completion(response.result)
                    }
            }
        }
    }
    
    func deleteMeasurement(measurementID: Int, completion:@escaping (Result<[MeasurementData], AFError>) -> ()) {
        Firebase.Auth.auth().currentUser?.getIDToken() { (idToken, error) in
            guard error == nil else {
//                completion(Result(catching: {
//                    throw (error! as AFError) // this will never be nil
//                }))
                return
            }
            if let token = idToken {
                let headers: HTTPHeaders = [
                    "Authorization": "Bearer \(token)",
                    "Content-Type": "application/json"
                ]
                let parameters: Dictionary<String, Int> = ["measurement_id": measurementID]
                
                AF.request("\(self.backendURL)/measurements", method: .delete, parameters: parameters, headers: headers)
                    .responseDecodable(of: [MeasurementData].self) { response in
                        completion(response.result)
                    }
            }
        }
    }
    
    func createMeasurement(data: MeasurementDataCreate, completion:@escaping (Result<MeasurementData, AFError>) -> ()) {
        if let jsonData = try? JSONEncoder().encode(data) {
            let measurementsDataArray = try! JSONSerialization.jsonObject(with: jsonData, options: [])
            Firebase.Auth.auth().currentUser?.getIDToken() { (idToken, error) in
                guard error == nil else {
//                    completion(nil, error)
                    return
                }
                if let token = idToken {
                    let headers: HTTPHeaders = [
                        "Authorization": "Bearer \(token)",
                        "Content-Type": "application/json"
                    ]
                    
                    AF.request("\(self.backendURL)/measurements", method: .post, parameters: (measurementsDataArray as! Parameters), encoding: JSONEncoding.default, headers: headers)
                        .responseDecodable(of: MeasurementData.self) { response in
                            completion(response.result)
                        }

                }
            }
        }

    }

}
