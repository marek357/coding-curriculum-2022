package com.ucl.hmssensyne.client

import kotlinx.serialization.*

class Models {
    @Serializable
    data class UserRequest(
        val user_completed_onboarding: Boolean?,
        val name: String?,
        val age: Int?
    )

    @Serializable
    data class MeasurementsRequest(
        val heart_rate_value: Float,
        val blood_pressure_systolic_value: Int?,
        val blood_pressure_diastolic_value: Int?,
        val timestamp: String,
    )

    @Serializable
    data class MeasurementsDateRequest(
        val start_date: String?,
        val end_date: String?,
    )

    @Serializable
    data class UserResponse(
        val user_completed_onboarding: Boolean,
        val name: String,
        val age: Int
    )

    @Serializable
    data class MeasurementsResponse(
        val heart_rate_value: Float,
        val blood_pressure_systolic_value: Float?,
        val blood_pressure_diastolic_value: Float?,
        val timestamp: String,
        val id: Int
    )
}
