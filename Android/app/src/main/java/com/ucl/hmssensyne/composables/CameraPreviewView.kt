package com.ucl.hmssensyne.composables

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.util.Size
import android.view.Surface.ROTATION_270
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.ucl.hmssensyne.client.createMeasurement
import com.ucl.hmssensyne.helpers.*
import com.ucl.hmssensyne.ui.theme.teal200
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.Executors

// https://github.com/gefilte/compose-photo-integration/

//TODO(@zcabda0): Add C++ inference integration and buttons and text to the screen.
@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalMaterialApi
@Composable
fun CameraPreviewView(
    context: Context,
    navController: NavController,
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    var hrResult = remember { mutableStateOf(-1.0) }
    var hrColor: Color = Green

    var measurementTaken = remember { mutableStateOf(false) }
    val measurementManager = MeasurementManager()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = BottomBar.ITEMS,
                navController = navController,
                onItemClick = {
                    navController.navigate(it.route)
                }
            )
        }
    ) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val previewView = PreviewView(context).apply {
                    this.scaleType = scaleType
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                // Preview
                val previewUseCase = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                coroutineScope.launch {
                    val cameraProvider = context.getCameraProvider()
                    try {
                        // Must unbind the use-cases before rebinding them.
                        cameraProvider.unbindAll()

                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, previewUseCase
                        )

                    } catch (ex: Exception) {
                        Log.e("CameraPreview", "Use case binding failed", ex)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        // enable the following line if RGBA output is needed.
                        // .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setTargetResolution(Size(1920, 1080))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setTargetRotation(ROTATION_270)
                        .build()

                    imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(),
                        ImageAnalysis.Analyzer { imageProxy ->
                            if (measurementTaken.value) {

                                if (measurementManager.finalValueMeasurement > 0) {
                                    measurementTaken.value = false

                                    try {
                                        createMeasurement(
                                            context = context,
                                            HRValue = measurementManager.finalValueMeasurement.toBigDecimal()
                                                .setScale(0, RoundingMode.UP).toFloat()
                                        )
                                        navController.navigate("history_page") {
                                            launchSingleTop = true
                                        }
                                    } catch (e: Exception) {
                                        e.message?.let { it1 -> Log.d("MeasurementError", it1) }
                                    }
                                }

                                @androidx.camera.core.ExperimentalGetImage
                                val bitmap: Bitmap = imageProxy.image!!.toBitmap()
                                val mat = Mat()
                                val grayscale = Mat()
                                Utils.bitmapToMat(bitmap, mat)
                                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB)
                                Imgproc.cvtColor(mat, grayscale, Imgproc.COLOR_RGB2GRAY)

                                handleFrame(mat.nativeObjAddr, grayscale.nativeObjAddr, 1)
                                measurementManager.resultLogicHandler()
                                hrResult.value = measurementManager.getHRValue()
                            }
                            imageProxy.close()
                        }
                    )
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, imageAnalysis, previewUseCase
                    )
                }
                previewView
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .offset(y = (-120).dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                Modifier
                    .fillMaxWidth(0.6f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                when {
                    hrResult.value == -1.0 -> {
                        Text(
                            text = "Unable finding face to track.",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    hrResult.value == 0.0 -> {
                        Text(
                            text = "Face found, please wait until the algorithm converges.",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    else -> {
                        when {
                            hrResult.value > 100 && hrResult.value < 40 -> {
                                hrColor = Red
                            }
                            else -> {
                                hrColor = Green
                            }
                        }
                        Text(
                            text = hrResult.value.toBigDecimal().setScale(0, RoundingMode.UP)
                                .toString(),
                            color = hrColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        )
                    }
                }
                Button(
                    onClick = {
                        measurementTaken.value = !measurementTaken.value
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(50.dp)
                        .clip(shape = RoundedCornerShape(20))
                ) {
                    if (!measurementTaken.value) {
                        Text(
                            text = "Start",
                            fontSize = 20.sp,
                            color = Blue
                        )
                    } else {
                        Text(
                            text = "Stop",
                            fontSize = 20.sp,
                            color = Red
                        )
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CustomColumn() {
    var bpmColor: Color = Green
    var bpmResult = remember { mutableStateOf(960.0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .offset(y = 20.dp)
            .background(teal200),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            Modifier
                .background(color = Color.LightGray)
                .fillMaxWidth(0.3f)
                .fillMaxHeight(0.2f)
                .clip(shape = RoundedCornerShape(30)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            bpmColor = if (bpmResult.value > 100 && bpmResult.value < 40) {
                Red
            } else {
                Green
            }
            Text(
                text = bpmResult.value.toBigDecimal().setScale(0, RoundingMode.UP).toString(),
                color = bpmColor,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
        }
    }
}

