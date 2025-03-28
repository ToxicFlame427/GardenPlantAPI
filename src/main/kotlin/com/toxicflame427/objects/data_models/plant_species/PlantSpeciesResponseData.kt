package com.toxicflame427.objects.data_models.plant_species

import kotlinx.serialization.Serializable

@Serializable
data class PlantSpeciesResponseData(
    val data : PlantSpeciesDetails?,
    val status : String,
    val message : String
)