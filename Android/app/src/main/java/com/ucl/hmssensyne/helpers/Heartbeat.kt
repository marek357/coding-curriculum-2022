package com.ucl.hmssensyne.helpers

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.databinding.BaseObservable
import com.ucl.hmssensyne.HRWrapper
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


val downsampleRate: Int = 1
val engine = HRWrapper()
val manager = MeasurementManager()
var initialised: Boolean = false

fun handleFrame(mat: Long, grayscale: Long, frameNumber: Int) {
    if (!initialised) {
        engine.initialise()
        initialised = true
    }

    if (frameNumber % downsampleRate == 0) {
        engine.getHR(mat = mat, grayscale = grayscale)
        manager.resultLogicHandler()
    }
}

fun destroyEngine() {
    initialised = false
    manager.restartMeasurement()
}

fun getHeartRateFromEngine(): Double {
    if (!initialised) {
        return -1.0
    }
    return engine.getHeartRateFromEngine()
}

fun getFaceDetectionStatusColor(
    mat: Long,
    grayscale: Long,
    runInference: Boolean
): Color? {
    if (!initialised) {
        return null
    } else {
        val hr = getHeartRateFromEngine()
        return when {
            hr > 0.0 -> {
                return Color.Green
            }
            hr == 0.0 -> {
                Color.Yellow
            }
            else -> {
                Color.Red
            }
        }
    }
}

fun getMeasurementManager(): MeasurementManager {
    return manager
}


class MeasurementManager : BaseObservable() {
    var finalValueMeasurement: Double = 0.0

    var showSuccessPopup: Boolean = false

    //    var userApi = UserApi()
    var readings: MutableList<Double> = mutableListOf()
    val MINIMAL_NUM_OF_READINGS: Int = 25
    val EPS: Double = 2.0


    fun resultLogicHandler() {
        val reading = getHeartRateFromEngine()
        Log.d("BPMAlg", reading.toString())
        Log.d("BPMAlg", readings.toString())

        when {
            reading > 50 -> {
                if (!readings.isNotEmpty()) {
                    readings = mutableListOf(reading)
                    return
                }
                if (readings.size == 1) {

                    if ((abs((readings.last())) - reading) < EPS) {
                        readings.add(reading)
                    } else {
                        readings = mutableListOf(reading)
                    }
                    return
                }
                val arithmeticMean = readings.sum() / readings.size.toDouble()
                val stdDeviationArr = readings.map { value -> (value - arithmeticMean).pow(2.0) }
                val variance = stdDeviationArr.sum()
                val stdDeviation = sqrt(variance)

                if (abs(arithmeticMean - reading) < stdDeviation) {
                    readings.add(reading)
                } else {
                    readings = mutableListOf(reading)
                }
            }
            else -> readings = mutableListOf()
        }

        if (readings.size >= MINIMAL_NUM_OF_READINGS) {
            finalValueMeasurement = readings.sum() / readings.size.toDouble()
        }
    }

    fun restartMeasurement() {
        readings = mutableListOf()
    }

    fun getHRValue(): Double {
        Log.d("BPMAlg", "Returning final measurement: " + finalValueMeasurement.toString())
        return finalValueMeasurement
    }

}



