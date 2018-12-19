package org.andcoe.adf

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.DeviceResourceUtils.Companion.resourceReturningDevices
import util.DeviceResourceUtils.Companion.resourceReturningEmptyDevices

class AppTest {

    @Test
    fun returnsEmptyJson() {
        withTestApplication(AppModule(resourceReturningEmptyDevices()).module()) {
            with(handleRequest(HttpMethod.Get, "/devices")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.content).isEqualTo("[]")
            }
        }
    }

    @Test
    fun returnsDevicesJson() {
        withTestApplication(AppModule(resourceReturningDevices()).module()) {
            with(handleRequest(HttpMethod.Get, "/devices")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.content).isEqualTo("""[{"deviceId":{"id":"PIXEL"}},{"deviceId":{"id":"SAMSUNG"}}]""")
            }
        }
    }

    @Test
    fun returnsDeviceByIdJson() {
        withTestApplication(AppModule(resourceReturningDevices()).module()) {
            with(handleRequest(HttpMethod.Get, "/devices/PIXEL")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                assertThat(response.content).isEqualTo("""{"deviceId":{"id":"PIXEL"}}""")
            }
        }
    }

    @Test
    fun returnsDeviceByIdAndHandlesNotFound() {
        withTestApplication(AppModule(resourceReturningDevices()).module()) {
            with(handleRequest(HttpMethod.Get, "/devices/some-random-device-id")) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
                assertThat(response.content).isEqualTo("""{"error":"Device with id: 'some-random-device-id' not found."}""")
            }
        }
    }
}