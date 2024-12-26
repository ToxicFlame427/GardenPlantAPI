@file:Suppress("DEPRECATION")

package com.toxicflame427.plugins

import com.toxicflame427.routes.plantSpeciesData
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        plantSpeciesData()

        get("/") {
            call.respondText("Welcome to the Garden Plants API! You can get started by getting an API key.")
        }

        static {
            resources("static")
        }
    }
}
