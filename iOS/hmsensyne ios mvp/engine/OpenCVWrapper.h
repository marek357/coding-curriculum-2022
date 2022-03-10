//
//  OpenCVWrapper.h
//  hmssensyneobjcppport
//
//  Created by Marek Masiak on 17/11/2021.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OpenCVWrapper : NSObject

- (void) initialise;
- (void) getHR:(UIImage *) imageBuffer;
- (NSString*) getInferenceTextFromEngine;
- (double) getHeartRateFromEngine;
    
@end

NS_ASSUME_NONNULL_END
