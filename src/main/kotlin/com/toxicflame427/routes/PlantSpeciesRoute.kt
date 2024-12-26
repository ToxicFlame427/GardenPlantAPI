@file:Suppress("t")

package com.toxicflame427.routes

import com.toxicflame427.objects.createOrUpdatePlant
import com.toxicflame427.objects.data_models.plant_species.PlantSpeciesDetails
import com.toxicflame427.objects.data_models.plant_species.PlantSpeciesResponseData
import com.toxicflame427.objects.data_models.plant_species_list_item.PlantListResponseData
import com.toxicflame427.objects.data_models.plant_species_list_item.PlantSpeciesListItem
import com.toxicflame427.objects.getListOfPlants
import com.toxicflame427.objects.getPlantById
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.plantSpeciesData(){
    route("/single-plant-species/{plantId}") {
        get {
            val plantId = call.parameters["plantId"]!!.toInt()
            val plant = getPlantById(plantId)

            // Make sure that API calls don't use numbers less than 1
            if(plantId > 0) {
                plant?.let {
                    call.respond(
                        HttpStatusCode.OK,
                        PlantSpeciesResponseData(
                            data = it,
                            status = "Completed",
                            message = "Retrieved plant ${it.commonName}"
                        )
                    )
                } ?: call.respond(
                    HttpStatusCode.OK,
                    PlantSpeciesResponseData(
                        data = null,
                        status = "Failed",
                        message = "No such plant could be found."
                    )
                )
            } else {
                // PlantId value was less than 1
                call.respond(
                    HttpStatusCode.OK,
                    PlantSpeciesResponseData(
                        data = null,
                        status = "Incomplete",
                        message = "PlantId value must be larger than 0"
                    )
                )
            }
        }
    }

    route("/plant-species-list/{limit}/{page}"){
        get {
            val limit = call.parameters["limit"]!!.toInt()
            val page = call.parameters["page"]!!.toInt()

            // Check to determine response type
            if(limit >= 1 && page >= 1) {
                // If data is provided, respond to API call accordingly
                val plantList = getListOfPlants(limit!!, page!!)
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
        }
    }

    route("/add-plant"){
        post{
            val request = try {
                call.receive<PlantSpeciesDetails>()
            } catch (e: ContentTransformationException){
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if(createOrUpdatePlant(request)){
                call.respond(
                    HttpStatusCode.OK,
                    "Plant added or updated successfully"
                )
            } else {
                call.respond(
                    HttpStatusCode.Conflict
                )
            }
        }
    }
}