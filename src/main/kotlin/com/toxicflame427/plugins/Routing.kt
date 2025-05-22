@file:Suppress("DEPRECATION")

package com.toxicflame427.plugins

import com.toxicflame427.objects.data_models.plant_species.PlantSpeciesResponseData
import com.toxicflame427.routes.plantSpeciesData
import com.toxicflame427.routes.suggestionsRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {
        plantSpeciesData()
        suggestionsRoute()

        get("/") {
            // The base URL to have some basic response, no data, no key required, just jeff
            call.respond(
                HttpStatusCode.OK,
                PlantSpeciesResponseData(
                    data = null,
                    status = "Completed",
                    message = "Welcome to Garden Plants API. Visit our website to create an SDK key and get started!"
                )
            )
        }

        // URL consists of... somehow still accessible after server shutdown? Local cache maybe
        // https://gardenplantsapi.online/resources/{plant_folder}/{image}.jpg
        //staticResources("resources", "images")
        staticFiles("/resources", File("E:/GardenAPIResources/images")) {
            enableAutoHeadResponse()
            // NO CACHE! This is what causes the images to not change!
            cacheControl { file ->
                listOf(CacheControl.MaxAge(maxAgeSeconds = 360))
            }
        }
    }
}
