package com.toxicflame427.objects.data_models.plant_species_list_item

import kotlinx.serialization.Serializable

@Serializable
data class PlantListResponseData(
    var data : List<PlantSpeciesListItem?>?,
    var itemCount : Int?,
    var page : Int?,
    var status : String,
    var message : String
)