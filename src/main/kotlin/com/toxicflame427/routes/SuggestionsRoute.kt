package com.toxicflame427.routes

import com.toxicflame427.objects.*
import com.toxicflame427.objects.data_models.PlantListResult
import com.toxicflame427.objects.data_models.PlantRequestModel
import com.toxicflame427.objects.data_models.plant_species.PlantSpeciesDetails
import com.toxicflame427.objects.data_models.plant_species.PlantSpeciesResponseData
import com.toxicflame427.objects.data_models.plant_species_list_item.PlantListResponseData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.suggestionsRoute(){
    route("/add-plant-request") {
        post{
            val key = call.parameters["key"]

            if(checkAdmin(key)){
                val request = try {
                    call.receive<PlantRequestModel>()
                } catch (e: ContentTransformationException){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                if(addPlantRequest(request)) {
                    call.respond(
                        HttpStatusCode.OK,
                        "Plant request successfully added"
                    )
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        "This request already exists: this should not occur EVER!"
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