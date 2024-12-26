package com.toxicflame427.objects.data_models.plant_species

import kotlinx.serialization.Serializable

@Serializable
data class MaturityTime(
    val time: String,
    val unit: String
)