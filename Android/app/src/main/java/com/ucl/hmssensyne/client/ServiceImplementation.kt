package com.ucl.hmssensyne.client

import android.util.Log
import com.ucl.hmssensyne.client.Models.*
import com.ucl.hmssensyne.helpers.getAccessToken
import io.ktor.client.*
import io.ktor.client.request.*

class ServiceImplementation(
    private val client: HttpClient
) : ServiceInterface {
    var token: String = ""

    override suspend fun getUser(): UserResponse {
        return client.get {
            url(HttpRoutes.USER)
            header("Authorization", "Bearer ${getAccessToken()}")
        }
    }

    override suspend fun createUser(userRequest: UserRequest): UserResponse {
        return client.post<UserResponse> {
            url(HttpRoutes.USER)
            header("Authorization", "Bearer ${getAccessToken()}")
            body = userRequest
        }
    }

    override suspend fun deleteUser(): UserResponse {
        return client.delete {
            url(HttpRoutes.USER)
            header("Authorization", "Bearer ${getAccessToken()}")
        }
    }

    override suspend fun updateUser(parameters: Map<String, Any>): UserResponse {
        return client.patch<UserResponse> {
            url(HttpRoutes.USER)
            header("Authorization", "Bearer ${getAccessToken()}")
            body = parameters
        }
    }

    override suspend fun getMeasurements(measurementsDateRequest: MeasurementsDateRequest): List<MeasurementsResponse> {
        return client.get {
            url(HttpRoutes.MEASUREMENT)
            header("Authorization", "Bearer ${getAccessToken()}")
            parameter("start_date", measurementsDateRequest.start_date)
            parameter("end_date", measurementsDateRequest.end_date)
        }
    }

    override suspend fun createMeasurement(measurementsRequest: MeasurementsRequest): MeasurementsResponse {
        return client.post<MeasurementsResponse> {
            url(HttpRoutes.MEASUREMENT)
            header("Authorization", "Bearer ${getAccessToken()}")
            body = measurementsRequest
        }
    }

    override suspend fun deleteMeasurement(measurement_id: Int): MeasurementsResponse {
        return client.delete {
            url(HttpRoutes.MEASUREMENT)
            header("Authorization", "Bearer ${getAccessToken()}")
            parameter("measurement_id", measurement_id)
        }
    }
}