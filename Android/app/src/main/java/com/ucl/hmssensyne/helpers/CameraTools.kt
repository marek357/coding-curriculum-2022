package com.ucl.hmssensyne.helpers

import android.content.Context
import android.graphics.*
import android.media.Image
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.*
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// https://stackoverflow.com/questions/56772967/converting-imageproxy-to-bitmap
fun Image.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val vuBuffer = planes[2].buffer // VU

    val ySize = yBuffer.remaining()
    val vuSize = vuBuffer.remaining()

    val nv21 = ByteArray(ySize + vuSize)

    yBuffer.get(nv21, 0, ySize)
    vuBuffer.get(nv21, ySize, vuSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            executor
        )
    }
}

val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)

// Method to save an image to external storage
@RequiresApi(Build.VERSION_CODES.R)
private fun saveImageToExternalStorage(bitmap: Bitmap) {
    // Get the external storage directory path
    val dcim = Environment.DIRECTORY_DCIM

    // Create a file to save the image
    val file = File(dcim, "${UUID.randomUUID()}.jpg")

    // Create the storage directory if it does not exist
    if (!file.exists() && !file.mkdirs()) {
        Log.d("MkdirFail", "Failed to create directory")
    }

    try {
        // Get the file output stream
        val stream: OutputStream = FileOutputStream(file)

        // Compress the bitmap
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        // Flush the output stream
        stream.flush()

        // Close the output stream
        stream.close()

        Log.d("MkdirSuccess", "Successfully captured image.")
    } catch (e: IOException) { // Catch the exception
        e.printStackTrace()
        e.message?.let { Log.d("IOException", it) }
    }
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}