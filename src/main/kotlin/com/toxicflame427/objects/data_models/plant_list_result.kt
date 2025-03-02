package com.toxicflame427.objects.data_models

import com.toxicflame427.objects.data_models.plant_species_list_item.PlantSpeciesListItem

data class PlantListResult(
    val plants: List<PlantSpeciesListItem?>,
    val totalCount: Int
)