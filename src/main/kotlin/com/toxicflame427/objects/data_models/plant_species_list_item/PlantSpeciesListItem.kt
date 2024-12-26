package com.toxicflame427.objects.data_models.plant_species_list_item

import com.toxicflame427.objects.data_models.plant_species.Image
import kotlinx.serialization.Serializable

@Serializable
data class PlantSpeciesListItem(
    val apiId: Int,
    val commonName: String,
    val otherNames: List<String>,
    val scientificName: String,
    val growingCycle: String,
    val images: List<Image>
)