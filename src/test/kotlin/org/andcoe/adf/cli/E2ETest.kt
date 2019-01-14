package org.andcoe.adf.cli

import io.mockk.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.andcoe.adf.core.CommandRunner
import org.andcoe.adf.core.mockAdbCommandsForDevice
import org.andcoe.adf.core.mockRestartAdb
import org.junit.Ignore
import org.junit.Test
import util.AdbOutput
import util.DeviceUtils
import java.io.PrintStream


class E2ETest {

    @Test
    @Ignore
    fun appStartsAndChecksForDevices() {
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


        val application = GlobalScope.async { org.andcoe.adf.main(emptyArray(), commandRunner) }

        val out = mockk<PrintStream>()

        every { out.println(any<String>()) } just Runs

        main("devices".split(" ").toTypedArray(), out)

        verify { out.println("PIXEL") }

        application.cancel()
    }
}