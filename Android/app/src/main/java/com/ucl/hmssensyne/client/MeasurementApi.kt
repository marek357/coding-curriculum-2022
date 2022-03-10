package com.ucl.hmssensyne.client

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ucl.hmssensyne.helpers.getAccessToken
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
suspend fun getMeasurements(): List<Models.MeasurementsResponse> {

    try {
        val token = getAccessToken()

        val client = HttpClient(Android) {
            install(Logging) {
                level = LogLevel.ALL
            }
        }
        val response: HttpResponse = client.request(HttpRoutes.MEASUREMENT) {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        client.close()

        return Json.decodeFromString(response.receive())

    } catch (e: Exception) {
        e.message?.let { Log.d("MGetError", it) }
        return listOf()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun createMeasurement(
    context: Context,
    HRValue: Float
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            withContext(Dispatchers.Main) {
                val token = getAccessToken()

                val client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                }

                val response: HttpResponse = client.request(HttpRoutes.MEASUREMENT) {
                    method = HttpMethod.Post
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    body = Json.encodeToString(
                        Models.MeasurementsRequest(
                            heart_rate_value = HRValue,
                            blood_pressure_systolic_value = 0,
                            blood_pressure_diastolic_value = 0,
                            timestamp = LocalDateTime.now().toString()
                        )
                    )
                }
                client.close()
                Toast.makeText(context, "Measurement created.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.message?.let { Log.d("MAddError", it) }
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun deleteMeasurement(
    context: Context,
    measurement_id: Int
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            withContext(Dispatchers.Main) {
                val token = getAccessToken()

                val client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                }
                val response: HttpResponse = client.request(HttpRoutes.MEASUREMENT) {
                    method = HttpMethod.Delete
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    body = Json.encodeToString(
                        mapOf("id" to measurement_id)
                    )
                }
                client.close()
                Toast.makeText(context, "HR data deleted.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.message?.let { Log.d("MDelError", it) }
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}

