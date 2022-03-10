package com.ucl.hmssensyne

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.otaliastudios.cameraview.CameraView
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class HRActivity : AppCompatActivity() {
    companion object {
        init {
            System.loadLibrary("hmssensyne")
        }
    }

    val hrWrapper = HRWrapper()

    fun loadToCache(fileName: String) {
        val f = File("$cacheDir/$fileName")
        if (!f.exists()) try {
            val `is`: InputStream = assets.open(fileName)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val fos = FileOutputStream(f)
            fos.write(buffer)
            fos.close()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.ucl.hmssensyne.R.layout.activity_main)
        loadToCache("deploy.prototxt")
        loadToCache("haarcascade_frontalface_alt.xml")
        loadToCache("res10_300x300_ssd_iter_140000.caffemodel")
//        /data/user/0/com.ucl.hmssensyne/cache
        val camera = findViewById<CameraView>(com.ucl.hmssensyne.R.id.camera)
        OpenCVLoader.initDebug()

        hrWrapper.initialise()
        camera.setLifecycleOwner(this)

        camera.addFrameProcessor { frame ->
            if (frame != null) {
                val data = frame.getData<ByteArray>()
                val size = frame.size
                val yuvImage = YuvImage(data, ImageFormat.NV21, size.width, size.height, null)
                val os = ByteArrayOutputStream()
                yuvImage.compressToJpeg(Rect(0, 0, size.width, size.height), 100, os)
                val jpegByteArray = os.toByteArray()
                var bitmap = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.size)
                val mat = Mat()
                val grayscale = Mat()
                Utils.bitmapToMat(bitmap, mat)
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB)
                Imgproc.cvtColor(mat, grayscale, Imgproc.COLOR_RGB2GRAY)
                hrWrapper.getHR(mat.nativeObjAddr, grayscale.nativeObjAddr)
            }
        }
    }
}
