package com.toxicflame427.objects.data_models

import kotlinx.serialization.Serializable

@Serializable
public final data class SDKKeys (
    val user: String,
    val key: String
)