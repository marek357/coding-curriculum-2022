//
//  SplashScreen.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 10/02/2022.
//

import Foundation
import SwiftUI


struct SplashScreen: View {
    var body: some View {
        VStack {
            Image("logo_transparent")
                .resizable()
                .scaledToFit()
                .frame(width: 400, height: 400)
        }
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
        .preferredColorScheme(.dark)
    }
}
