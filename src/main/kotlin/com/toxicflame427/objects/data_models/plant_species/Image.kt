package com.toxicflame427.objects.data_models.plant_species

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val credit: String,
    val imageLicense: String,
    val originalUrl: String,
    val url: String
)