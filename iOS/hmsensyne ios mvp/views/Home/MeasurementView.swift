//
//  Measurement.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 12/02/2022.
//

import Foundation
import SwiftUI
import ExytePopupView

struct MeasurementView: View {
    @ObservedObject var authorizationManager : AuthorizationManager
    @ObservedObject var inferenceState: InferenceState = InferenceState(runInference: false)
    @ObservedObject var measurementManager: MeasurementManager = getMeasurementManager()
    @EnvironmentObject var measurementStore: MeasurementStore
    
    var body: some View {
        VStack {
            ZStack {
                PreviewCameraHolder(inferenceState)
                    .cornerRadius(15)
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(getFaceDetectionStatusColor(runInference: inferenceState.runInference) ?? Color.accentColor, lineWidth: 4)
                    )
                    .padding()
            }
            .popup(isPresented: $measurementManager.showSuccessPopup, autohideIn: 2) {
                VStack {
                    HStack {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundColor(Color.green)
                            .font(.system(size: 32, weight: .regular, design: .default))
                        Text("Success")
                            .foregroundColor(Color.white)
                            .font(.system(size: 20, weight: .regular, design: .default))
                    }
                    .padding(.bottom, 10)
                    Text("The heart rate reading is: \(String(format: "%.2f", measurementManager.finalValueMeasurement))")
                        .foregroundColor(Color.white)
                        .font(.system(size: 20, weight: .regular, design: .default))
                }
                .frame(width: 325, height: 100)
                .background(Color(red: 33 / 255, green: 78 / 255, blue: 117 / 255))
                .cornerRadius(8.0)
            }
            .onChange(of: measurementManager.finalValueMeasurement) { newValue in
                guard newValue > 0 else { return }
                inferenceState.stopInference()
                inferenceState.clearInferenceText()
                destroyEngine()
                inferenceState.handleFinalInferenceResult(measurementStore: measurementStore)
            }
            
            Spacer()
            Button(action: {
                if inferenceState.runInference {
                    inferenceState.stopInference()
                    inferenceState.clearInferenceText()
                    destroyEngine()
                } else {
                    inferenceState.startInference()
                }
            }) {
                VStack {
                    Image(systemName: "waveform.path.ecg")
                        .font(.system(size: 40, weight: .heavy, design: .default))
                        .foregroundColor(inferenceState.runInference ? .green : nil)
                }.padding(10.0)
                    .overlay(
                        Circle()
                            .stroke(lineWidth: 2.0)
                            .foregroundColor(inferenceState.runInference ? .green : nil)
                    )
            }.padding(.bottom, 20.0)
        }
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
        .cornerRadius(15)
        .padding()
    }
    
}

