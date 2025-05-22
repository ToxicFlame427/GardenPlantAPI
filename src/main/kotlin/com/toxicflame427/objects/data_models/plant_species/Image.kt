package com.toxicflame427.objects.data_models.plant_species

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val credit: String,
    val imageLicense: String,
    val licenseCode: String?,
    val originalUrl: String,
    val smallUrl: String?,
    val url: String
)