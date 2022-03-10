package com.ucl.hmssensyne.composables

import android.content.Context
import android.graphics.PointF
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ucl.hmssensyne.R
import com.ucl.hmssensyne.client.Models.MeasurementsResponse
import com.ucl.hmssensyne.client.getMeasurements
import com.ucl.hmssensyne.ui.theme.purple200
import com.ucl.hmssensyne.ui.theme.purple700
import java.time.Duration
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterialApi
@Composable
fun HistoryView(
    context: Context,
    navController: NavController,
    auth: FirebaseAuth
) {
    val lineData: List<MeasurementsResponse>

    val products = produceState(
        initialValue = emptyList<MeasurementsResponse>(),
        producer = {
            value = getMeasurements()
        }
    )

    lineData = products.value

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(purple200)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LineChart(lineData)
            }
            LazyColumn(
                modifier = Modifier.offset(y = 200.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(
                    items = lineData,
                    itemContent = {
                        DataRow(data = it)
                    })
            }
        }
    }
}

// https://github.com/MakeItEasyDev/Jetpack-Compose-Line-Chart
// https://www.waseefakhtar.com/android/recyclerview-in-jetpack-compose/
// https://github.com/Madrapps/plot

//TODO(@zcabda0): Find a way to label chart axis, make it scrollable and have the timeline indicator.
@Composable
fun LineChart(data: List<MeasurementsResponse>) {
    val lineChartData = data
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .offset(x = 10.dp, y = 5.dp)
        ) {
            Text(text = "Heart Rate", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Text(text = "Heart Rate Recordings", fontSize = 15.sp)
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentSize(align = Alignment.BottomStart)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val distance = size.width / (lineChartData.size + 1)
                var currentX = 0F
                val maxValue = 200f
                val points = mutableListOf<PointF>()

                lineChartData.forEachIndexed { index, data ->
                    if (lineChartData.size >= index + 2) {
                        val y0 = (maxValue - data.heart_rate_value) * (size.height / maxValue)
                        val x0 = currentX + distance
                        points.add(PointF(x0, y0))
                        currentX += distance
                    }
                }

                for (i in 0 until points.size - 1) {
                    drawLine(
                        start = Offset(points[i].x, points[i].y),
                        end = Offset(points[i + 1].x, points[i + 1].y),
                        color = purple700,
                        strokeWidth = 8f
                    )
                }
            }
        }
    }
}

//TODO(@zcabda0): Make the cards slidable for quick deletion.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DataRow(data: MeasurementsResponse) {
    var color: Color

    if ((data.heart_rate_value in 81.0..99.0) || (data.heart_rate_value in 40.0..50.0)) {
        color = Yellow
    }
    if (data.heart_rate_value >= 100 || data.heart_rate_value < 40) {
        color = Red
    } else {
        color = Green
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth(),
        border = BorderStroke(2.dp, Black),
        elevation = 2.dp,
        backgroundColor = White,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))

    ) {
        Row {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .align(Alignment.CenterVertically)
            ) {
                Image(
                    painterResource(R.drawable.logo_transparent),
                    contentDescription = "heart_logo"
                )
            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                Text(
                    text = "Avg HR:"
                )
                Text(
                    text = data.heart_rate_value.toString(),
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
                    .weight(1.5f)
            ) {
                Column {
                    Text(text = "Min/Max BP:")
                }
                Column {
                    Text(
                        text = "${data.blood_pressure_systolic_value}/${data.blood_pressure_diastolic_value}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                val durationText: String
                val duration: Duration =
                    Duration.between(LocalDateTime.parse(data.timestamp), LocalDateTime.now())
                if (duration.toDays().toInt() > 0) {
                    durationText = "${duration.toDays()} days ago"
                } else if ((duration.toDays().toInt() == 0) && (duration.toHours() > 0)) {
                    durationText = "${duration.toHours()} hours ago"
                } else {
                    durationText = "${duration.toMinutes()} minutes ago"
                }
                Text(text = durationText)
            }
        }
    }
}
