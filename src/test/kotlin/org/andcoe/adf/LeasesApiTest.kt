package org.andcoe.adf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.andcoe.adf.devices.*
import org.andcoe.adf.leases.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.DeviceUtils.Companion.DEVICE_PIXEL
import util.DeviceUtils.Companion.DEVICE_SAMSUNG
import util.JsonAssertion.Companion.assertThatJson
import java.util.*

class LeasesApiTest {

    @Test
    fun createsLeaseForAnyDevice() {
        val appModule = devicesWithoutLeasesScenario()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.Created)

                val leaseId =
                    jacksonObjectMapper().readValue<Map<String, Any>>(response.content!!)["leaseId"] as Map<String, Any>
                val id = leaseId["id"] as String

                assertThatJson(response.content)
                    .isEqualTo(
                        """{
                            "leaseId": {
                                "id": "$id"
                            },
                            "device": {
                                "deviceId": {
                                    "id": "PIXEL"
                                },
                                "model": "Pixel",
                                "manufacturer": "Google",
                                "androidVersion": "9.0",
                                "apiLevel": "28",
                                "port": "7777"
                            }
                      }"""
                    )
            }
        }
    }

    @Test
    fun returnsErrorWhenNoDevicesAvailable() {
        val appModule = noDevicesScenario()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.BadRequest)
                assertThat(response.content).isEqualTo("""{"error":"No devices available to lease."}""")
            }
        }
    }

    @Test
    fun returnsErrorIfAllDevicesAlreadyLeased() {
        val appModule = allDevicesLeasedScenario()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.BadRequest)
                assertThat(response.content).isEqualTo("""{"error":"No devices available to lease."}""")
            }
        }
    }

    @Test
    fun createsLeaseForSpecificDevice() {
        val appModule = devicesWithoutLeasesScenario()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases/${DEVICE_PIXEL.deviceId.id}")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.Created)

                val leaseId =
                    jacksonObjectMapper().readValue<Map<String, Any>>(response.content!!)["leaseId"] as Map<*, *>
                val id = leaseId["id"] as String

                assertThatJson(response.content)
                    .isEqualTo(
                        """{
                            "leaseId": {
                                "id": "$id"
                            },
                            "device": {
                                "deviceId": {
                                    "id": "PIXEL"
                                },
                                "model": "Pixel",
                                "manufacturer": "Google",
                                "androidVersion": "9.0",
                                "apiLevel": "28",
                                "port": "7777"
                            }
                      }"""
                    )
            }
        }
    }

    @Test
    fun returnsErrorIfSpecificDeviceIsAlreadyLeased() {
        val appModule = allDevicesLeasedScenario()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases/${DEVICE_PIXEL.deviceId.id}")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.BadRequest)
                assertThat(response.content).isEqualTo("""{"error":"No devices available to lease."}""")
            }
        }
    }

    @Test
    fun returnsErrorWhenSpecificDeviceIdNotFound() {
        val appModule = allDevicesLeasedScenario()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases/some-random-device-id")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
                assertThat(response.content).isEqualTo("""{"error":"No device found with id: 'some-random-device-id'"}""")
            }
        }
    }

    @Test
    fun returnsAllLeases() {
        val leaseId1 = LeaseId("123")
        val leaseId2 = LeaseId("456")
        val appModule = allDevicesLeasedScenario(
            leaseId1 = leaseId1,
            leaseId2 = leaseId2
        )

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Get, "/leases")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThatJson(response.content)
                    .isEqualTo(
                        """[
                            {
                                "device": {
                                    "androidVersion": "9.0",
                                    "apiLevel": "28",
                                    "deviceId": {
                                        "id": "PIXEL"
                                    },
                                    "manufacturer": "Google",
                                    "model": "Pixel",
                                    "port": "7777"
                                },
                                "leaseId": {
                                    "id": "123"
                                }
                            },
                            {
                                "device": {
                                    "androidVersion": "8.1",
                                    "apiLevel": "27",
                                    "deviceId": {
                                        "id": "SAMSUNG"
                                    },
                                    "manufacturer": "Samsung",
                                    "model": "S9",
                                    "port": "7778"
                                },
                                "leaseId": {
                                    "id": "456"
                                }
                            }
                        ]"""
                    )
            }
        }
    }

    private fun noDevicesScenario(): (Application) -> Unit {
        val devicesDb = mutableMapOf<DeviceId, Device>()
        val deviceDao = DevicesDao(devicesDb)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesDb)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        return AppModule(
            devicesResource = deviceResource,
            leasesResource = leasesResource
        ).module()
    }

    private fun devicesWithoutLeasesScenario(): (Application) -> Unit {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DevicesDao(devicesDb)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesDb)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        return AppModule(
            devicesResource = deviceResource,
            leasesResource = leasesResource
        ).module()
    }


    private fun allDevicesLeasedScenario(
        leaseId1: LeaseId = LeaseId(UUID.randomUUID().toString()),
        leaseId2: LeaseId = LeaseId(UUID.randomUUID().toString())
    ): (Application) -> Unit {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DevicesDao(devicesDb)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)

        val leasesDb = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )
        val leaseDao = LeasesDao(leasesDb)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        return AppModule(
            devicesResource = deviceResource,
            leasesResource = leasesResource
        ).module()
    }

}