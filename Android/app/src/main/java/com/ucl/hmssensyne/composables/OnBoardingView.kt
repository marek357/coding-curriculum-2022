package com.ucl.hmssensyne.composables

import android.content.Context
import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.ucl.hmssensyne.R
import com.ucl.hmssensyne.client.ServiceInterface
import com.ucl.hmssensyne.client.updateUser
import com.ucl.hmssensyne.ui.theme.*
import java.util.*

data class OnBoardingData(
    val image: Int, val title: String,
    val desc: String,
    val color: Color
)

// https://github.com/AndroidLibrariesYoutube/ComposeHorizontalPager-Complete
//TODO(@zcabda0): Resize the images and choose better color palette for each screen.
@ExperimentalPagerApi
@Composable
fun OnBoardingPage(context: Context, navController: NavController) {
    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize()) {

        val items = ArrayList<OnBoardingData>()

        items.add(
            OnBoardingData(
                R.drawable.ship,
                "Measure vitals anywhere",
                "Travel without any additional equipment",
                shipColor
            )
        )

        items.add(
            OnBoardingData(
                R.drawable.ai,
                "AI powered measurements",
                "Get measurements from AI-based technology",
                aiColor
            )
        )

        items.add(
            OnBoardingData(
                R.drawable.watch,
                "Fast measurements",
                "Get reliable measurements in up to 15 seconds",
                watchColor
            )
        )


        items.add(
            OnBoardingData(
                R.drawable.gym,
                "Check your vitals on the go",
                "Become the best version of yourself",
                onboardingfont

            )
        )

        val pagerState = rememberPagerState(
            pageCount = items.size,
            initialOffscreenLimit = 2,
            infiniteLoop = false,
            initialPage = 0,
        )

        OnBoardingView(
            context = context,
            navController = navController,
            item = items,
            pagerState = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        )

    }
}

@ExperimentalPagerApi
@Composable
fun OnBoardingView(
    context: Context,
    navController: NavController,
    item: List<OnBoardingData>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HorizontalPager(state = pagerState) { page ->
                Column(
                    modifier = Modifier
                        .padding(top = 60.dp)
                        .fillMaxWidth()
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = item[page].image),
                        contentDescription = item[page].title,
                        modifier = Modifier
                            .height(300.dp)
                            .offset(y = 100.dp)
                            .fillMaxWidth(0.8f)
                    )

                    Text(
                        text = item[page].title,
                        modifier = Modifier.padding(top = 120.dp), color = onboardingfont,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = item[page].desc,
                        modifier = Modifier.padding(top = 15.dp, start = 20.dp, end = 20.dp),
                        color = onboardingfont,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                }
            }

            PagerIndicator(item.size, pagerState.currentPage)
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomSection(context = context, pagerState.currentPage, navController)
        }
    }
}

@ExperimentalPagerApi
@Composable
fun rememberPagerState(
    @androidx.annotation.IntRange(from = 0) pageCount: Int,
    @androidx.annotation.IntRange(from = 0) initialPage: Int = 0,
    @FloatRange(from = 0.0, to = 1.0) initialPageOffset: Float = 0f,
    @androidx.annotation.IntRange(from = 1) initialOffscreenLimit: Int = 1,
    infiniteLoop: Boolean = false
): PagerState = rememberSaveable(saver = PagerState.Saver) {
    PagerState(
        pageCount = pageCount,
        currentPage = initialPage,
        currentPageOffset = initialPageOffset,
        offscreenLimit = initialOffscreenLimit,
        infiniteLoop = infiniteLoop
    )
}

@Composable
fun PagerIndicator(size: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(top = 60.dp)
    ) {
        repeat(size) {
            Indicator(isSelected = it == currentPage)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    val width = animateDpAsState(targetValue = if (isSelected) 25.dp else 10.dp)

    Box(
        modifier = Modifier
            .padding(1.dp)
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colors.primary else purple200.copy(alpha = 0.5f)
            )
    )
}

@Composable
fun BottomSection(
    context: Context,
    currentPager: Int,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = if (currentPager != 3) Arrangement.SpaceBetween else Arrangement.Center
    ) {

        if (currentPager == 3) {
            OutlinedButton(
                onClick = { },
                shape = RoundedCornerShape(50), // = 40% percent
            ) {
                Text(
                    text = "Get Started",
                    modifier = Modifier
                        .clickable(onClick = {
                            val parameters = mapOf("user_completed_onboarding" to true)
                            updateUser(
                                context = context,
                                parameters = parameters
                            )
                            navController.navigate("camera_preview_page") {
                                launchSingleTop = true
                            }
                        })
                        .padding(vertical = 8.dp, horizontal = 40.dp),
                    color = purple500
                )
            }
        } else {
            SkipNextButton("Skip", Modifier.padding(start = 20.dp))
            SkipNextButton("Next", Modifier.padding(end = 20.dp))
        }

    }
}

//TODO(@zcabda0): Add functionality to Skip/Next button.
@Composable
fun SkipNextButton(text: String, modifier: Modifier) {
    Text(
        text = text, color = purple200, modifier = modifier, fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    )

}
