package com.ucl.hmssensyne

class HRWrapper {
    companion object {
        init {
            System.loadLibrary("hmssensyne")
        }
    }

    external fun getHR(mat: Long, grayscale: Long)
    external fun getHeartRateFromEngine(): Double
    external fun initialise()
    external fun sanityCheck(): Int
}
