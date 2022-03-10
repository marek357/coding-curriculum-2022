package com.ucl.hmssensyne.client

import com.ucl.hmssensyne.client.Models.*
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.http.*

// https://github.com/philipplackner/KtorClientAndroid

interface ServiceInterface {

    suspend fun getUser(): UserResponse

    suspend fun createUser(userRequest: UserRequest): UserResponse?

    suspend fun deleteUser(): UserResponse

    suspend fun updateUser(parameters: Map<String, Any>): UserResponse?

    suspend fun getMeasurements(measurementsDateRequest: MeasurementsDateRequest): List<MeasurementsResponse>

    suspend fun createMeasurement(measurementsRequest: MeasurementsRequest): MeasurementsResponse?

    suspend fun deleteMeasurement(measurement_id: Int): MeasurementsResponse

    companion object {
        fun create(): ServiceInterface {
            return ServiceImplementation(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                    install(HttpTimeout) {
                        socketTimeoutMillis = 15_000
                        requestTimeoutMillis = 15_000
                        connectTimeoutMillis = 15_000
                    }
                    defaultRequest {
//                        contentType(ContentType.Application.Json)
                    }
                }
            )
        }
    }
}