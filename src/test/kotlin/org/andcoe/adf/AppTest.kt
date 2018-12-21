package org.andcoe.adf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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

class AppTest {

    @Test
    fun returnsEmptyJson() {
        val devicesDb = mutableMapOf<DeviceId, Device>()
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            deviceResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Get, "/devices")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.content).isEqualTo("[]")
            }
        }
    }

    @Test
    fun returnsDevicesJson() {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            deviceResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Get, "/devices")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThatJson(response.content)
                    .isEqualTo(
                        """[
                            {
                                "deviceId": {
                                    "id": "PIXEL"
                                },
                                "model": "Pixel",
                                "manufacturer": "Google",
                                "androidVersion": "9.0",
                                "apiLevel": "28",
                                "port": "7777"
                                },
                            {
                                "deviceId": {
                                    "id": "SAMSUNG"
                                },
                                "model": "S9",
                                "manufacturer": "Samsung",
                                "androidVersion": "8.1",
                                "apiLevel": "27",
                                "port": "7778"
                            }
                        ]"""
                    )
            }
        }
    }

    @Test
    fun returnsDeviceByIdJson() {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            deviceResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Get, "/devices/PIXEL")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThatJson(response.content)
                    .isEqualTo(
                        """{
                            "deviceId": {
                                "id": "PIXEL"
                            },
                            "model": "Pixel",
                            "manufacturer": "Google",
                            "androidVersion": "9.0",
                            "apiLevel": "28",
                            "port": "7777"
                      }"""
                    )
            }
        }
    }

    @Test
    fun returnsDeviceByIdAndHandlesNotFound() {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            deviceResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Get, "/devices/some-random-device-id")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
                assertThat(response.content).isEqualTo("""{"error":"Device with id: 'some-random-device-id' not found."}""")
            }
        }
    }

    @Test
    fun createsLeaseForAnyDevice() {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            deviceResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.Created)

                val leaseId = jacksonObjectMapper().readValue<Map<String, Any>>(response.content!!)["leaseId"] as Map<String, Any>
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
        val devicesDb = mutableMapOf<DeviceId, Device>()
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            deviceResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.BadRequest)
                assertThat(response.content).isEqualTo("""{"error":"No devices available to lease."}""")
            }
        }
    }

    @Test
    fun returnsErrorIfAllDevicesAlreadyLeased() {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())
        
        val leasesDb = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            deviceResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.BadRequest)
                assertThat(response.content).isEqualTo("""{"error":"No devices available to lease."}""")
            }
        }
    }

    @Test
    fun createsLeaseForSpecificDevice() {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            deviceResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases/${DEVICE_PIXEL.deviceId.id}")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.Created)

                val leaseId = jacksonObjectMapper().readValue<Map<String, Any>>(response.content!!)["leaseId"] as Map<String, Any>
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
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesDb = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            deviceResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases/${DEVICE_PIXEL.deviceId.id}")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.BadRequest)
                assertThat(response.content).isEqualTo("""{"error":"No devices available to lease."}""")
            }
        }
    }

    @Test
    fun returnsErrorWhenSpecificDeviceIdNotFound() {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesDb = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            deviceResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Post, "/leases/some-random-device-id")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
                assertThat(response.content).isEqualTo("""{"error":"No device found with id: 'some-random-device-id'"}""")
            }
        }
    }

}