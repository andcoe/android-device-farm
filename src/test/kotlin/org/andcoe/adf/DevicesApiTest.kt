package org.andcoe.adf

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_PIXEL
import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_SAMSUNG
import org.andcoe.adf.devices.*
import org.andcoe.adf.leases.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.JsonAssertion.Companion.assertThatJson

class DevicesApiTest {

    @Test
    fun returnsEmptyJson() {
        val devicesStore = mutableMapOf<DeviceId, Device>()
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)

        val leasesStore = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            devicesResource = deviceResource,
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
        val devicesStore = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)

        val leasesStore = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            devicesResource = deviceResource,
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
        val devicesStore = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)

        val leasesStore = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            devicesResource = deviceResource,
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
        val devicesStore = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)

        val leasesStore = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leasesResource = LeasesResource(leaseService)

        val appModule = AppModule(
            devicesResource = deviceResource,
            leasesResource = leasesResource
        ).module()

        withTestApplication(appModule) {
            with(handleRequest(HttpMethod.Get, "/devices/some-random-device-id")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
                assertThat(response.content)
                    .isEqualTo("""{"error":"Device with id: 'some-random-device-id' not found."}""")
            }
        }
    }

}