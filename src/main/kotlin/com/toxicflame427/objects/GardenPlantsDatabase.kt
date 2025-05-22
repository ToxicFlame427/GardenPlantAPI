package com.toxicflame427.objects

import com.mongodb.client.model.ReplaceOptions
import com.toxicflame427.objects.data_models.plant_species.PlantSpeciesDetails
import com.toxicflame427.objects.data_models.plant_species_list_item.PlantSpeciesListItem
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import com.mongodb.client.model.Filters.*
import com.toxicflame427.objects.data_models.PlantListResult
import com.toxicflame427.objects.data_models.PlantRequestModel
import com.toxicflame427.objects.data_models.SDKKeys
import io.ktor.util.logging.*
import kotlin.math.ceil

private val client = KMongo.createClient().coroutine

// Databases
private val database = client.getDatabase("GardenPlantsDatabase")

// Collections
private val sdkKeys = database.getCollection<SDKKeys>()
private val plants = database.getCollection<PlantSpeciesDetails>()
private val requests = database.getCollection<PlantRequestModel>()

suspend fun checkSDKKey(key: String?) : Boolean {
    // If there is no key, then reject access
    if(key == null){
        return false;
    }

    // If a key is present, check database for matches.
    val attempt = sdkKeys.find(eq(SDKKeys::key.name, key)).first() ?: return false

    // Always check to see if the SDK key matches before returning true!
    return attempt.key == key
}

suspend fun checkAdmin(key: String?) : Boolean{
    // If there is no key, then reject access
    if(key == null){
        return false;
    }

    // If a key is present, check database for matches. IF no key exists, then restrict access
    val attempt = sdkKeys.find(eq(SDKKeys::key.name, key)).first() ?: return false

    // Check to see if the SDK key is tied to an admin account, grant access if true
    return (attempt.user == "admin" && attempt.key == key)
}

suspend fun getPlantById(id: Int): PlantSpeciesDetails?{
    return plants.find(eq(PlantSpeciesDetails::apiId.name, id)).first()
}

suspend fun getListOfPlants(limit: Int, page: Int, filterQuery: String?, searchQuery: String?): PlantListResult {
    val skip = (page - 1) * limit

    // Build the filter based on filterQuery
    val filter = when (filterQuery?.lowercase()) {
        "species" -> eq(PlantSpeciesListItem::speciesOrVariety.name, "Species")
        "variety" -> eq(PlantSpeciesListItem::speciesOrVariety.name, "Variety")
        "both" -> or(
            eq(PlantSpeciesListItem::speciesOrVariety.name, "Species"),
            eq(PlantSpeciesListItem::speciesOrVariety.name, "Variety")
        )
        else -> empty() // No filter if filterQuery is invalid or null
    }

    // Example of combining with a searchQuery (if provided) for name or scientificName:
    val combinedFilter = if (!searchQuery.isNullOrBlank()) {
        and(
            filter,
            or(
                regex(PlantSpeciesListItem::name.name, searchQuery, "i"), // Case-insensitive regex search
                regex(PlantSpeciesListItem::scientificName.name, searchQuery, "i")
            )
        )
    } else {
        filter
    }

    // This one gets the full unlimited list; Used of total page calulations
    val aggregateList = plants.find(combinedFilter)
        .toList() // Directly map to a list of PlantSpeciesListItem
        .map {
            PlantSpeciesListItem(
                apiId = it.apiId,
                name = it.name,
                otherNames = it.otherNames,
                scientificName = it.scientificName,
                growingCycle = it.growingCycle,
                speciesOrVariety = it.speciesOrVariety,
                images = it.images
            )
        }

    // Thi is the limited list that is returned to the user
    val aggregateLimitList = plants.find(combinedFilter)
        .skip(skip)
        .limit(limit)
        .toList() // Directly map to a list of PlantSpeciesListItem
        .map {
            PlantSpeciesListItem(
                apiId = it.apiId,
                name = it.name,
                otherNames = it.otherNames,
                scientificName = it.scientificName,
                growingCycle = it.growingCycle,
                speciesOrVariety = it.speciesOrVariety,
                images = it.images
            )
        }

    // I have no clue why I have to add 1 for this to work, but it works with this :)
    val totalCount = ceil((aggregateList.size / limit).toDouble()) + 1;

    // It should be ok to convert this number to an int, as it cannot have floating point results after a ceiling round
    return PlantListResult(aggregateLimitList, totalCount.toInt());
}

suspend fun createOrUpdatePlant(plant: PlantSpeciesDetails): Boolean{
    val filter = eq("apiId", plant.apiId)
    //print(plant) // Find out if the field is actually correct here, and it is!
    val result = plants.replaceOne(
        filter,
        plant,
        ReplaceOptions().upsert(true)
    )

    return result.wasAcknowledged()
}

suspend fun addPlantRequest(requestBody: PlantRequestModel): Boolean{
    val filter = eq("id", requestBody.id)
    //print(plant) // Find out if the field is actually correct here, and it is!
    val result = requests.replaceOne(
        filter,
        requestBody,
        ReplaceOptions().upsert(true)
    )

    return result.wasAcknowledged()
}