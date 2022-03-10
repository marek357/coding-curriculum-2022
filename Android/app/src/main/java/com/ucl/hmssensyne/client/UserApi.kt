package com.ucl.hmssensyne.client

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


////TODO(@zcabda0): Add API connection for user data tracking, user data create/read/update/delete, etc.
fun getUser(
    context: Context,
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            withContext(Dispatchers.Main) {
                val token = getAccessToken()

                val client = HttpClient(Android){
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                }

                val response: HttpResponse = client.request(HttpRoutes.USER) {
                    method = HttpMethod.Get
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
//                    contentType(ContentType.Application.Json)
                }
                client.close()

                val formattedResponse = response.receive<String>()
                Toast.makeText(context, formattedResponse, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.message?.let { Log.d("UUpError", it) }
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}

fun createUser(
    context: Context,
    auth: FirebaseAuth,
    name: String,
    email: String,
    age: String,
    password: String
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            withContext(Dispatchers.Main) {
                auth.createUserWithEmailAndPassword(email, password).await()
                val token = getAccessToken()

                val client = HttpClient(Android){
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                }

                val response: HttpResponse = client.request(HttpRoutes.USER) {
                    method = HttpMethod.Post
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    body = Json.encodeToString(
                        Models.UserRequest(
                            user_completed_onboarding = false,
                            name = name,
                            age = age.toInt()
                        )
                    )
                }
                client.close()
                Toast.makeText(context, "Successfully registered.", Toast.LENGTH_LONG)
                    .show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.message?.let { Log.d("RegError", it) }
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}


fun updateUser(
    context: Context,
    parameters: Map<String, Any>
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            withContext(Dispatchers.Main) {
                val token = getAccessToken()

                val client = HttpClient(Android){
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                }

                val response: HttpResponse = client.request(HttpRoutes.USER) {
                    method = HttpMethod.Post
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    body = Json.encodeToString(
                        parameters
                    )
                }
                client.close()
                Toast.makeText(context, "User updated.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.message?.let { Log.d("UUpError", it) }
//                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}

fun deleteUser(
    context: Context
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            withContext(Dispatchers.Main) {
                val token = getAccessToken()

                val client = HttpClient(Android){
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                }

                val response: HttpResponse = client.request(HttpRoutes.USER) {
                    method = HttpMethod.Delete
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
//                    contentType(ContentType.Application.Json)
                }
                client.close()
                Toast.makeText(context, "User deleted.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.message?.let { Log.d("UDelError", it) }
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
