package com.toxicflame427.objects.data_models.plant_species

import kotlinx.serialization.Serializable

@Serializable
data class PlantUsage(
    val description: String,
    val use: String
)