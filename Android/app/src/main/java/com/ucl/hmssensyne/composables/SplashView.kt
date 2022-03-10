package com.ucl.hmssensyne.composables

import android.content.Context
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ucl.hmssensyne.R
import kotlinx.coroutines.delay

// https://www.geeksforgeeks.org/animated-splash-screen-in-android-using-jetpack-compose/


//TODO(@zcabda0): Add vector image instead of png for the logo.
@Composable
fun SplashView(context: Context, navController: NavController, auth: FirebaseAuth) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 2f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = {
                    OvershootInterpolator(6f).getInterpolation(it)
                })
        )
        delay(3000L)
        navController.navigate("login_page")
    }
    // Image
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}