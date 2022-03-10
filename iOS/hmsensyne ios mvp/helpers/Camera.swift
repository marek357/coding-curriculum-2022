//
//  Camera.swift
//  hmsensyne ios mvp
//
//  Created by Marek Masiak on 09/02/2022.
//

import Foundation
import UIKit
import AVFoundation
import SwiftUI

typealias ImageBufferHandler = ((_ imageBuffer: CMSampleBuffer) -> ())

// adapted from: https://stackoverflow.com/questions/61255857/how-to-get-camera-frame-to-process-using-swiftui
final class PreviewCameraView: UIView ,AVCaptureVideoDataOutputSampleBufferDelegate{
    private var captureSession: AVCaptureSession?
    var imageBufferHandler: ImageBufferHandler?
    private var videoConnection: AVCaptureConnection!
    private var frameNumber: Int = 0
    private var runInference: InferenceState
    
    init(runInference: InferenceState) {
        self.runInference = runInference
        super.init(frame: .zero)
        
        if !self.requestVideoAccess() {
            print("No access to camera") // no fatal error, because conscious user choice
            return
        }
        
        if !self.configureCameraInput() {
            fatalError("Unable to configure camera input") // fatal error, because device error
        }

        if !self.configureCameraOutput() {
            fatalError("Unable to configure camera output") // fatal error, because device error
        }
    }
    
    func requestVideoAccess() -> Bool {
        var allowedAccess = false
        let blocker = DispatchGroup()
        blocker.enter()
        AVCaptureDevice.requestAccess(for: .video) { accessRequestResult in
            allowedAccess = accessRequestResult
            blocker.leave()
        }
        blocker.wait()
        return allowedAccess
    }
    
    func configureCameraInput() -> Bool {
        let session = AVCaptureSession()
        session.beginConfiguration()
        let videoDevice = AVCaptureDevice.default(.builtInWideAngleCamera,
                                                  for: .video, position: .front)
        guard videoDevice != nil, let videoDeviceInput = try? AVCaptureDeviceInput(device: videoDevice!), session.canAddInput(videoDeviceInput) else {
            return false
        }
        session.addInput(videoDeviceInput)
        session.commitConfiguration()
        self.captureSession = session
        return true
    }
    
    func configureCameraOutput() -> Bool {
        let videoDataOutput = AVCaptureVideoDataOutput()
        videoDataOutput.videoSettings = [kCVPixelBufferPixelFormatTypeKey : NSNumber(value: kCVPixelFormatType_32BGRA)] as [String : Any]
        videoDataOutput.alwaysDiscardsLateVideoFrames = true
        videoDataOutput.setSampleBufferDelegate(self, queue: DispatchQueue(label: "com.sensynehealth.facereadings.queue"))
        guard self.captureSession!.canAddOutput(videoDataOutput) else {
            return false
        }
        self.captureSession!.addOutput(videoDataOutput)
        self.videoConnection = videoDataOutput.connection(with: .video)
        return true
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override class var layerClass: AnyClass {
        AVCaptureVideoPreviewLayer.self
    }
    
    var videoPreviewLayer: AVCaptureVideoPreviewLayer {
        return layer as! AVCaptureVideoPreviewLayer
    }
    
    override func didMoveToSuperview() {
        super.didMoveToSuperview()
        
        if nil != self.superview {
            self.videoPreviewLayer.session = self.captureSession
            self.videoPreviewLayer.videoGravity = .resize
            self.captureSession?.startRunning()
            
        }
        else {
            self.captureSession?.stopRunning()
        }
    }
    
    func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
        if !self.runInference.runInference {
            return
        }

        if connection.videoOrientation != .portrait {
            connection.videoOrientation = .portrait
            return
        }
        guard let frame: CVPixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else {
            debugPrint("unable to get image from sample buffer")
            return
        }

        let ciimage : CIImage = CIImage(cvPixelBuffer: frame)
        let image : UIImage = self.convert(cmage: ciimage)

        self.frameNumber += 1
        handleFrame(frame: image, frameNumber: self.frameNumber)
        self.runInference.setInferenceText(text: getInferenceTextFromEngine())
    }

    private func convert(cmage:CIImage) -> UIImage {
        let context:CIContext = CIContext.init(options: nil)
        let cgImage:CGImage = context.createCGImage(cmage, from: cmage.extent)!
        let image:UIImage = UIImage.init(cgImage: cgImage, scale: 1.0, orientation: .right)
        return image
    }

}

struct PreviewCameraHolder: UIViewRepresentable {
    @ObservedObject var runInference: InferenceState

    init(_ runInference: InferenceState) {
        self.runInference = runInference
    }
    
    func makeUIView(context: UIViewRepresentableContext<PreviewCameraHolder>) -> PreviewCameraView {
        PreviewCameraView(runInference: self.runInference)
    }

    func updateUIView(_ uiView: PreviewCameraView, context: UIViewRepresentableContext<PreviewCameraHolder>) {
    }

    typealias UIViewType = PreviewCameraView
}

