package com.toxicflame427.objects.data_models.plant_species

import kotlinx.serialization.Serializable

@Serializable
data class CompanionPlant(
    val apiId: Int,
    val companionshipReason: String,
    val name: String,
    val scientificName: String
)