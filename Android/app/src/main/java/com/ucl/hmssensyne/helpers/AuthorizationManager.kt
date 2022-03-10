package com.ucl.hmssensyne.helpers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ucl.hmssensyne.client.HttpRoutes
import com.ucl.hmssensyne.client.Models
import io.ktor.client.*
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun loginUser(
    context: Context,
    auth: FirebaseAuth,
    email: String,
    password: String
) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    auth.signInWithEmailAndPassword(email, password).await()
                    Toast.makeText(context, "Successfully logged in.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.message?.let { Log.d("LoginError", it) }
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
}

fun checkLoggedInState(auth: FirebaseAuth) {
    if (auth.currentUser == null) { // not logged in
        Log.d("LoginFail", "You are not logged in")
    } else {
        Log.d("LoginSuccess", "You are logged in!")
    }
}

suspend fun checkOnboardingState(state: Boolean) {
    try {
        withContext(Dispatchers.Main) {
            val token = getAccessToken()

            val client = HttpClient(Android){
                install(Logging) {
                    level = LogLevel.ALL
                }
            }
            val response: Models.UserResponse = client.request(HttpRoutes.USER) {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            client.close()
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            e.message?.let { Log.d("OnboardCheckError", it) }
        }
    }
}

suspend fun getAccessToken(): String? {
    return try {
        var accessToken: String = "this_is_an_access_token"
        val result = FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()
        if (result != null) {
            accessToken = result.token.toString()
        }
        accessToken
    } catch (e: Exception) {
        e.message?.let { Log.d("getTokenError", it) }
        null
    }
}
