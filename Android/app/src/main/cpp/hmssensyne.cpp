// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("hmssensyne");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("hmssensyne")
//      }
//    }

#include <jni.h>
#include <iostream>
#import <opencv2/opencv.hpp>
#include "RPPG.hpp"

extern "C" {
RPPG rppg;

JNIEXPORT void Java_com_ucl_hmssensyne_HRWrapper_initialise(
        JNIEnv *env,
        jobject /* this */
) {

    std::cout << "RPPG init begin" << std::endl;
    rppg = RPPG();
    //TODO check if files available
    rppg.load(
            rPPGAlgorithm::g, faceDetAlgorithm::deep,
            cv::CAP_PROP_FRAME_WIDTH, cv::CAP_PROP_FRAME_HEIGHT,
            0.001, 5, 1, 1, 5,
            5, ".", "/data/user/0/com.ucl.hmssensyne/cache/haarcascade_frontalface_alt.xml",
            "/data/user/0/com.ucl.hmssensyne/cache/deploy.prototxt",
            "/data/user/0/com.ucl.hmssensyne/cache/res10_300x300_ssd_iter_140000.caffemodel",
            false, false
    );
    std::cout << "RPPG init completed" << std::endl;
}

JNIEXPORT jint Java_com_ucl_hmssensyne_HRWrapper_sanityCheck(
        JNIEnv *env,
        jobject
) {
    return 42;
}


JNIEXPORT void Java_com_ucl_hmssensyne_HRWrapper_getHR(
        JNIEnv *env,
        jobject /* this */,
        jlong matImageAddr,
        jlong grayscaleImageAddr
) {
    Mat *matImage = (Mat *) matImageAddr;
    Mat *grayscaleImage = (Mat *) grayscaleImageAddr;

    int time = (cv::getTickCount() * 1000.0) / cv::getTickFrequency();
    rppg.processFrame(*matImage, *grayscaleImage, time);
}

JNIEXPORT double Java_com_ucl_hmssensyne_HRWrapper_getHeartRateFromEngine(
        JNIEnv *env,
        jobject /* this */
) {
    return rppg.getHeartRate();
}
}