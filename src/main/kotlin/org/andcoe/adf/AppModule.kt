package org.andcoe.adf

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.JacksonConverter
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import org.andcoe.adf.devices.DeviceResource

class AppModule(private val deviceResource: DeviceResource) {

    fun module(): Application.() -> Unit = {

        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter())
        }

        install(StatusPages) {
            //            exception<ResourceNotFound> { call.respond(HttpStatusCode.NotFound) }
        }

        routing {
            get("/devices") {
                val devices = deviceResource.devices()
                call.respond(HttpStatusCode.OK, devices)
            }
        }
    }


}
