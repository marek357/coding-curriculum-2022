package com.ucl.hmssensyne

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.auth.FirebaseAuth
import com.ucl.hmssensyne.client.ServiceInterface
import com.ucl.hmssensyne.composables.*
import com.ucl.hmssensyne.helpers.Permission
import com.ucl.hmssensyne.ui.theme.HMSSensyneTheme
import org.opencv.android.OpenCVLoader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


//TODO(@zcabda0): Clean-up imports for the whole project.
@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
class MainActivity : AppCompatActivity() {

//    private val service = ServiceInterface.create()

    companion object {
        init {
            System.loadLibrary("hmssensyne")
        }
    }

    private fun loadToCache(fileName: String) {
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

    lateinit var auth: FirebaseAuth
    var successfulLogin = false


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        loadToCache("deploy.prototxt")
        loadToCache("haarcascade_frontalface_alt.xml")
        loadToCache("res10_300x300_ssd_iter_140000.caffemodel")

        OpenCVLoader.initDebug()

        auth = FirebaseAuth.getInstance()
        auth.signOut()

        setContent {
            HMSSensyneTheme {
                val navController = rememberNavController()
                Permission()
                HSMSensyneNavigator(
                    navController = navController,
                    auth = auth
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @ExperimentalPagerApi
    @ExperimentalComposeUiApi
    @Composable
    fun HSMSensyneNavigator(
        navController: NavHostController,
        auth: FirebaseAuth
    ) {

        NavHost(
            navController = navController,
            startDestination = "splash_page",
            builder = {
                composable(
                    "splash_page",
                    content = {
                        SplashView(
                            context = applicationContext,
                            navController = navController,
                            auth = auth
                        )
                    })
                composable(
                    "login_page",
                    content = {
                        LoginView(
                            context = applicationContext,
                            navController = navController,
                            auth = auth
                        )
                    })
                composable(
                    "register_page",
                    content = {
                        SignUpView(
                            context = applicationContext,
                            navController = navController,
                            auth = auth
                        )
                    })
                composable(
                    "onboarding_page",
                    content = {
                        OnBoardingPage(
                            context = applicationContext,
                            navController = navController
                        )
                    })
                composable(
                    "history_page",
                    content = {
                        HistoryView(
                            context = applicationContext,
                            navController = navController,
                            auth = auth
                        )
                    })
                composable(
                    "camera_preview_page",
                    content = {
                        CameraPreviewView(
                            context = applicationContext,
                            navController = navController
                        )
                    })
                composable(
                    "settings_page",
                    content = {
                        SettingsView(
                            context = applicationContext,
                            navController = navController,
                            auth = auth
                        )
                    })
            })
    }
}
