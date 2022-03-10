//
//  OpenCVWrapper.m
//  hmssensyneobjcppport
//
//  Created by Marek Masiak on 17/11/2021.
//

#ifdef __cplusplus

#import <opencv2/opencv.hpp>

#import <opencv2/imgcodecs/ios.h>

#import <opencv2/videoio/cap_ios.h>

#endif

#import "OpenCVWrapper.h"
#import "RPPG.hpp"


@implementation OpenCVWrapper

RPPG rppg;

- (void) initialise {
    NSFileManager *filemgr;
    filemgr = [NSFileManager defaultManager];
    NSString* haarFilePath = [[NSBundle mainBundle] pathForResource:@"haarcascade_frontalface_alt" ofType:@"xml"];
    NSString* dnnProtoFilePath = [[NSBundle mainBundle] pathForResource:@"deploy" ofType:@"prototxt"];
    NSString* dnnModelFilePath = [[NSBundle mainBundle] pathForResource:@"res10_300x300_ssd_iter_140000" ofType:@"caffemodel"];
    const char *haarPath = [haarFilePath UTF8String];
    const char *dnnProtoPath = [dnnProtoFilePath UTF8String];
    const char *dnnModelPath = [dnnModelFilePath UTF8String];
    
    std::ifstream test2(dnnProtoPath);
    if (!test2) {
        std::cout << "DNN proto file not found!" << std::endl;
        exit(0);
    }

    std::ifstream test3(dnnModelPath);
    if (!test3) {
        std::cout << "DNN model file not found!" << std::endl;
        exit(0);
    }

    rppg = RPPG();
    rppg.load(
      rPPGAlgorithm::g, faceDetAlgorithm::deep,
      cv::CAP_PROP_FRAME_WIDTH, cv::CAP_PROP_FRAME_HEIGHT,
      0.001, 5, 1, 1, 5, 5, ".", haarPath, dnnProtoPath,
      dnnModelPath, false, false
    );
}

- (void) getHR:(UIImage*)image {
    cv::Mat matImage = [OpenCVWrapper cvMatFromUIImage:image];
    cv::Mat frameGray;
    
    cvtColor(matImage, frameGray, COLOR_BGR2GRAY);
    equalizeHist(frameGray, frameGray);
    
    int time = (cv::getTickCount()*1000.0)/cv::getTickFrequency();
    @try {
        rppg.processFrame(matImage, frameGray, time);
    }
    @catch (NSException *exception) {
        [OpenCVWrapper initialize];
    }
}

// https://github.com/hungrxyz/RealtimeOCR/blob/master/OpenCVT/CVWrapper.mm
+ (cv::Mat)cvMatFromUIImage:(UIImage *)image
{
    CGColorSpaceRef colorSpace = CGImageGetColorSpace(image.CGImage);
    CGFloat cols = image.size.width;
    CGFloat rows = image.size.height;
    
    cv::Mat cvMat(rows, cols, CV_8UC4); // 8 bits per component, 4 channels (color channels + alpha)
    
    CGContextRef contextRef = CGBitmapContextCreate(cvMat.data,                 // Pointer to  data
                                                    cols,                       // Width of bitmap
                                                    rows,                       // Height of bitmap
                                                    8,                          // Bits per component
                                                    cvMat.step[0],              // Bytes per row
                                                    colorSpace,                 // Colorspace
                                                    kCGImageAlphaNoneSkipLast |
                                                    kCGBitmapByteOrderDefault); // Bitmap info flags
    
    CGContextDrawImage(contextRef, CGRectMake(0, 0, cols, rows), image.CGImage);
    CGContextRelease(contextRef);
    cv::cvtColor(cvMat, cvMat, COLOR_RGBA2RGB);

    return cvMat;
}

- (NSString*) getInferenceTextFromEngine {
    return [NSString stringWithUTF8String: rppg.getInferenceTextFromEngine()];
}

- (double) getHeartRateFromEngine {
    return rppg.getHeartRate();
}

@end
