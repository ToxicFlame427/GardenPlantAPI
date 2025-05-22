package com.toxicflame427.objects.data_models

import kotlinx.serialization.Serializable

@Serializable
data class PlantRequestModel(
    val id: String,
    val dateSubmitted: String,
    val requestedPlants: String
)
