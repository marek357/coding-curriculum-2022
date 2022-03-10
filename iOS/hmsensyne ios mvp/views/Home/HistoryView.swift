//
//  History.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 12/02/2022.
//

import Foundation
import SwiftUI
import SwiftUICharts

struct HistoryViewRow: View {
    let data: MeasurementData
    
    var body: some View {
        HStack {
            if let hr_value = data.heart_rate_value {
                Image(systemName: "heart.fill")
                    .foregroundColor(hr_value > 50 && hr_value < 90 ? Color.green : Color.red)
                    .font(.system(size: 30))
                VStack {
                    Text("\(String(hr_value))")
                        .font(.system(size: 30, weight: .heavy, design: .default))
                    Text("Heart Rate")
                        .font(.system(size: 12))
                }
                .padding(.leading, 20.0)
            }
            Spacer()
            if Calendar.current.numberOfDaysBetween(from: stringToDate(timestamp: data.timestamp), to: Date.now) > 0 {
                Text("\(Calendar.current.numberOfDaysBetween(from: stringToDate(timestamp: data.timestamp), to: Date.now)) days ago")
            } else if Calendar.current.dateComponents([.hour], from: stringToDate(timestamp: data.timestamp), to: Date.now).hour ?? 0 > 0 {
                Text("\(Calendar.current.dateComponents([.hour], from: stringToDate(timestamp: data.timestamp), to: Date.now).hour ?? 0) hours ago")
            } else {
                Text("\(Calendar.current.dateComponents([.minute], from: stringToDate(timestamp: data.timestamp), to: Date.now).minute ?? 0) minutes ago")
            }
        }
    }
}

struct HistoryView: View {
    @ObservedObject var authorizationManager : AuthorizationManager
    @EnvironmentObject var measurementStore: MeasurementStore
    let apiHelper: UserApi = UserApi()
    var demoData: [Double] = [8, 2, 4, 6, 12, 9, 2]
    
    var body: some View {
        VStack {
            LineChartView(data: measurementStore.getMeasurementData(), title: "Heart Rate", legend: "Heart Rate readings", form: ChartForm.large, rateValue: 0)
                .padding()
            List {
                ForEach(measurementStore.measurements) { measurement in
                    if measurement.heart_rate_value != nil {
                        HistoryViewRow(data: measurement)
                    }
                }.onDelete(perform: delete)
            }
            .refreshable {
                print("fetching")
                fetch()
            }
            .navigationBarTitle(Text("Search"))
            .onAppear(perform: fetch)
            .background(Color(red: 242 / 255, green: 111 / 255, blue: 98 / 255))
            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
            .cornerRadius(15)
            .padding()
        }
        .onAppear(perform: fetch)
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
    }
    
    private func delete(at offsets: IndexSet) {
        for idx in offsets {
            measurementStore.deleteMeasurement(idx: idx)
        }
    }
    
    private func fetch() {
        measurementStore.fetch(startDate: nil, endDate: nil)
    }
}

