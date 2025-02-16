@file:Suppress("t")

package com.toxicflame427.routes

import com.toxicflame427.objects.*
import com.toxicflame427.objects.data_models.plant_species.PlantSpeciesDetails
import com.toxicflame427.objects.data_models.plant_species.PlantSpeciesResponseData
import com.toxicflame427.objects.data_models.plant_species_list_item.PlantListResponseData
import com.toxicflame427.objects.data_models.plant_species_list_item.PlantSpeciesListItem
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.plantSpeciesData(){
    route("/single-plant-species/{plantId}") {
        get {
            val plantId = call.parameters["plantId"]?.toIntOrNull() ?: 1 // Default will be 1
            val key = call.parameters["key"] // Can be nullable, but access will be restricted

            // Before calculating data response, make sure a valid API key is being used
            if(checkSDKKey(key)) {
                // Only get the plant if the SDK key is valid
                val plant = getPlantById(plantId)

                // Make sure that API calls don't use numbers less than 1
                if (plantId > 0) {
                    plant?.let {
                        call.respond(
                            HttpStatusCode.OK,
                            PlantSpeciesResponseData(
                                data = it,
                                status = "Completed",
                                message = "Retrieved plant ${it.name}"
                            )
                        )
                    } ?: call.respond(
                        HttpStatusCode.OK,
                        PlantSpeciesResponseData(
                            data = null,
                            status = "Completed - no data",
                            message = "No such plant could be found."
                        )
                    )
                } else {
                    // PlantId value was less than 1
                    call.respond(
                        HttpStatusCode.OK,
                        PlantSpeciesResponseData(
                            data = null,
                            status = "Complete - no data",
                            message = "PlantId value must be larger than 0"
                        )
                    )
                }
            } else {
                // If the SDK key is not valid, return the response stating so.
                call.respond(
                    HttpStatusCode.Forbidden,
                    PlantSpeciesResponseData(
                        data = null,
                        status = "Complete - no data",
                        message = "SDK key is invalid. Make sure you are using the correct API key."
                    )
                )
            }
        }
    }

    route("/plant-species-list"){
        get {
            val limit = call.parameters["limit"]?.toIntOrNull() ?: 10 // Default to 10
            val page = call.parameters["page"]?.toIntOrNull() ?: 1 // Default to 1
            val key = call.parameters["key"] // Default set to null, is required for a response

            // Nullable, as they are not required
            val filterQuery = call.parameters["fq"]
            val searchQuery = call.parameters["sq"]

            val plantList: List<PlantSpeciesListItem?>

            if(checkSDKKey(key)) {
                // Check to determine response type
                if (limit >= 1 && page >= 1) {
                    // If data is provided, respond to API call accordingly
                    plantList = getListOfPlants(limit, page, filterQuery, searchQuery)

                    plantList.let {
                        call.respond(
                            HttpStatusCode.OK,
                            PlantListResponseData(
                                data = plantList,
                                itemCount = limit,
                                page = page,
                                status = "Completed",
                                message = "Completed with $limit items on page $page "
                            )
                        )
                    }
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        PlantListResponseData(
                            data = null,
                            itemCount = 0,
                            page = 0,
                            status = "Incomplete",
                            message = "Limit and page values must be larger than 0"
                        )
                    )
                }
            } else {
                call.respond(
                    HttpStatusCode.Forbidden,
                    PlantListResponseData(
                        data = null,
                        itemCount = 0,
                        page = 1,
                        status = "Completed - No data",
                        message = "SDK Key is invalid"
                    )
                )
            }
        }
    }

    route("/add-plant"){
        post{
            val key = call.parameters["key"];

            if(checkAdmin(key)) {
                val request = try {
                    call.receive<PlantSpeciesDetails>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                if (createOrUpdatePlant(request)) {
                    call.respond(
                        HttpStatusCode.OK,
                        "Plant added or updated successfully"
                    )
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        "This plant data point already exists"
                    )
                }
            } else {
                call.respond(
                    HttpStatusCode.Forbidden,
                    "Only admins are allowed to use this endpoint. An admin key is required."
                )
            }
        }
    }
}