package com.toxicflame427.objects.data_models.plant_species

import kotlinx.serialization.Serializable

@Serializable
data class TemperatureTolerance(
    val maxTemp: Int,
    val minTemp: Int,
    val unit: String
)