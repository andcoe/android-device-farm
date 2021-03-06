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
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.andcoe.adf.devices.DevicesResource
import org.andcoe.adf.exceptions.DeviceNotFound
import org.andcoe.adf.exceptions.NoDevicesAvailableToLease
import org.andcoe.adf.exceptions.ResourceNotFound
import org.andcoe.adf.leases.LeasesResource

class AppModule(
    private val devicesResource: DevicesResource,
    private val leasesResource: LeasesResource
) {

    fun module(): Application.() -> Unit = {

        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter())
        }

        install(StatusPages) {
            exception<ResourceNotFound> { call.respond(HttpStatusCode.NotFound, mapOf("error" to it.message)) }
            exception<NoDevicesAvailableToLease> { call.respond(HttpStatusCode.BadRequest, mapOf("error" to it.message)) }
            exception<DeviceNotFound> { call.respond(HttpStatusCode.NotFound, mapOf("error" to it.message)) }
        }

        routing {
            get("/devices") {
                call.respond(HttpStatusCode.OK, devicesResource.devices())
            }
            get("/devices/{deviceId}") {
                val deviceId: String = call.parameters["deviceId"]!!
                call.respond(HttpStatusCode.OK, devicesResource.devices(deviceId))
            }
            get("/leases") {
                call.respond(HttpStatusCode.OK, leasesResource.leases())
            }
            post("/leases") {
                call.respond(HttpStatusCode.Created, leasesResource.create())
            }
            post("/leases/{deviceId}") {
                val deviceId: String = call.parameters["deviceId"]!!
                call.respond(HttpStatusCode.Created, leasesResource.create(deviceId))
            }
            delete("/leases/{leaseId}") {
                val leaseId: String = call.parameters["leaseId"]!!
                leasesResource.delete(leaseId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }


}
