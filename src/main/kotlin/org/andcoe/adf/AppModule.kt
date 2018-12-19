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
import org.andcoe.adf.exceptions.ResourceNotFound

class AppModule(private val deviceResource: DeviceResource) {

    fun module(): Application.() -> Unit = {

        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter())
        }

        install(StatusPages) {
            exception<ResourceNotFound> { call.respond(HttpStatusCode.NotFound, mapOf("error" to it.message)) }
        }

        routing {
            get("/devices") {
                call.respond(HttpStatusCode.OK, deviceResource.devices())
            }
            get("/devices/{deviceId}") {
                val deviceId: String = call.parameters["deviceId"]!!
                call.respond(HttpStatusCode.OK, deviceResource.devices(deviceId))
            }
        }
    }


}
