package org.andcoe.adf.cli

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.andcoe.adf.core.CommandRunner
import org.andcoe.adf.core.mockAdbCommandsForDevice
import org.junit.Test
import util.AdbOutput
import org.andcoe.adf.core.mockRestartAdb
import org.andcoe.adf.devices.Device
import util.DeviceUtils
import java.net.ConnectException

class E2ETest {

    @Test
    fun doStuff() {
        val commandRunner = mockk<CommandRunner>()
        commandRunner.mockRestartAdb()
        commandRunner.mockAdbCommandsForDevice(
            deviceId = DeviceUtils.DEVICE_PIXEL.deviceId,
            tcpIpPort = 7777,
            adbModelResponse = AdbOutput.ADB_DEVICE_MODEL_PIXEL,
            adbManufacturerResponse = AdbOutput.ADB_DEVICE_MANUFACTURER_GOOGLE,
            adbAndroidVersionResponse = AdbOutput.ADB_ANDROID_VERSION_PIXEL,
            adbApiLevelResponse = AdbOutput.ADB_API_LEVEL_PIXEL
        )
        commandRunner.mockAdbCommandsForDevice(
            deviceId = DeviceUtils.DEVICE_SAMSUNG.deviceId,
            tcpIpPort = 7778,
            adbModelResponse = AdbOutput.ADB_DEVICE_MODEL_S9,
            adbManufacturerResponse = AdbOutput.ADB_DEVICE_MANUFACTURER_SAMSUNG,
            adbAndroidVersionResponse = AdbOutput.ADB_ANDROID_VERSION_S9,
            adbApiLevelResponse = AdbOutput.ADB_API_LEVEL_S9
        )
        every { commandRunner.exec("adb devices") } returns AdbOutput.ADB_DEVICES.output


        val application = GlobalScope.async {org.andcoe.adf.main(emptyArray(), commandRunner) }


        val client = HttpClient() {
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
        }

        runBlocking {
            var notConnected = true
            while(notConnected) {
                delay(500)
                try {


                    val result = client.get<List<Device>>("http://0.0.0.0:8000/devices")
                    println("*******************************************************************")
                    println("*******************************************************************")
                    println("*******************************************************************")
                    println("*******************************************************************")
                    println(result)

                    println("*******************************************************************")
                    println("*******************************************************************")
                    println("*******************************************************************")
                    println("*******************************************************************")
                    notConnected = false
                } catch (e: ConnectException) {
                    println("NOT READY")

                }
            }
        }

        application.cancel()




    }
}