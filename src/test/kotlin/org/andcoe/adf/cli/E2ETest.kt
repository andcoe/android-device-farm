package org.andcoe.adf.cli

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_PIXEL
import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_SAMSUNG
import org.andcoe.adf.core.LocalCommandRunner
import org.andcoe.adf.devices.Device
import org.andcoe.adf.devices.DeviceId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class E2ETest {

    @Test
    fun appStartsAndChecksForDevices() {
        val application = GlobalScope.async { org.andcoe.adf.main(emptyArray(), LocalCommandRunner()) }

        Thread.sleep(500)

        assertThat(getDevices()).isEqualTo(
            listOf(
                DEVICE_PIXEL,
                DEVICE_SAMSUNG
            )
        )

        application.cancel()
    }

    private fun getDevices(): List<Device> {
        val client = HttpClient { install(JsonFeature) { serializer = JacksonSerializer() } }
        return runBlocking { client.get<List<Device>>("http://127.0.0.1:8000/devices") }
    }
}