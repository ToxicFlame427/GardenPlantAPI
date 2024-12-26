package com.toxicflame427.objects

import com.mongodb.client.model.Filters.eq
import com.toxicflame427.objects.data_models.plant_species.PlantSpeciesDetails
import com.toxicflame427.objects.data_models.plant_species_list_item.PlantSpeciesListItem
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("GardenPlantsDatabase")
private val plants = database.getCollection<PlantSpeciesDetails>()

suspend fun getPlantById(id: Int): PlantSpeciesDetails?{
    return plants.find(eq(PlantSpeciesDetails::apiId.name, id)).first()
}

suspend fun getListOfPlants(limit: Int, page: Int): List<PlantSpeciesListItem?>{
    val list = mutableListOf<PlantSpeciesListItem>()

    for(i in 0..<limit){
        plants.find(eq(PlantSpeciesListItem::apiId.name, (i + 1) + (limit * (page - 1)))).first()?.let {
            list.add(i, PlantSpeciesListItem(
                apiId = it.apiId,
                commonName = it.commonName,
                otherNames = it.otherNames,
                scientificName = it.scientificName,
                growingCycle = it.growingCycle,
                images = it.images
            ))
        }
    }

    return list
}

suspend fun createOrUpdatePlant(plant: PlantSpeciesDetails): Boolean{
    val plantExists = plants.findOneById(plant.apiId) != null
    return if(plantExists){
        plants.updateOneById(plant.apiId, plant).wasAcknowledged()
    } else {
        plant.apiId = plant.apiId
        plants.insertOne(plant).wasAcknowledged()
    }
}