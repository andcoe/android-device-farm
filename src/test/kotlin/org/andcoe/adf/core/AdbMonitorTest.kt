package org.andcoe.adf.core

import io.mockk.*
import org.andcoe.adf.devices.Device
import org.andcoe.adf.devices.DeviceId
import org.andcoe.adf.devices.DevicesService
import org.junit.Test
import util.AdbOutput
import util.AdbOutput.*
import util.DeviceUtils.Companion.ADB_PIXEL
import util.DeviceUtils.Companion.ADB_SAMSUNG
import util.DeviceUtils.Companion.DEVICE_PIXEL
import util.DeviceUtils.Companion.DEVICE_SAMSUNG

class AdbMonitorTest {

    private val commandRunner: CommandRunner = mockk()
    private val devicesService: DevicesService = mockk()
    private val adbMonitor = AdbMonitor(
        devicesService = devicesService,
        adb = Adb(commandRunner)
    )

    @Test
    fun restartsAdbAndRunsCommands() {
        commandRunner.mockRestartAdb()

        adbMonitor.restartAdb()

        commandRunner.verifyRestartAdb()
    }


    @Test
    fun handlesNoDevices() {
        every { devicesService.devices() } returns emptyMap()
        adbMonitor.refreshDevicesWith(listOf())
        verify { devicesService.devices() }
    }

    @Test
    fun handlesNewDeviceConnected() {
        commandRunner.mockAdbCommandsForDevice(
            deviceId = DEVICE_PIXEL.deviceId,
            tcpIpPort = 7777,
            adbModelResponse = ADB_DEVICE_MODEL_PIXEL,
            adbManufacturerResponse = ADB_DEVICE_MANUFACTURER_GOOGLE,
            adbAndroidVersionResponse = ADB_ANDROID_VERSION_PIXEL,
            adbApiLevelResponse = ADB_API_LEVEL_PIXEL
        )
        every { devicesService.devices() } returns emptyMap()
        every { devicesService.create(ADB_PIXEL) } returns DEVICE_PIXEL

        adbMonitor.refreshDevicesWith(listOf(DEVICE_PIXEL.deviceId))

        verify { devicesService.devices() }
        verify(exactly = 1) { devicesService.create(ADB_PIXEL) }
        commandRunner.verifyAdbCommandsForDevice(deviceId = DEVICE_PIXEL.deviceId, tcpIpPort = 7777)
    }

    @Test
    fun handlesMultipleNewDevicesConnected() {
        commandRunner.mockAdbCommandsForDevice(
            deviceId = DEVICE_PIXEL.deviceId,
            tcpIpPort = 7777,
            adbModelResponse = ADB_DEVICE_MODEL_PIXEL,
            adbManufacturerResponse = ADB_DEVICE_MANUFACTURER_GOOGLE,
            adbAndroidVersionResponse = ADB_ANDROID_VERSION_PIXEL,
            adbApiLevelResponse = ADB_API_LEVEL_PIXEL
        )
        commandRunner.mockAdbCommandsForDevice(
            deviceId = DEVICE_SAMSUNG.deviceId,
            tcpIpPort = 7778,
            adbModelResponse = ADB_DEVICE_MODEL_S9,
            adbManufacturerResponse = ADB_DEVICE_MANUFACTURER_SAMSUNG,
            adbAndroidVersionResponse = ADB_ANDROID_VERSION_S9,
            adbApiLevelResponse = ADB_API_LEVEL_S9
        )
        every { devicesService.devices() } returns emptyMap()
        every { devicesService.create(ADB_PIXEL) } returns DEVICE_PIXEL
        every { devicesService.create(ADB_SAMSUNG) } returns DEVICE_SAMSUNG

        adbMonitor.refreshDevicesWith(listOf(DEVICE_PIXEL.deviceId, DEVICE_SAMSUNG.deviceId))

        verify { devicesService.devices() }
        verify(exactly = 1) { devicesService.create(ADB_PIXEL) }
        verify(exactly = 1) { devicesService.create(ADB_SAMSUNG) }
        commandRunner.verifyAdbCommandsForDevice(deviceId = DEVICE_PIXEL.deviceId, tcpIpPort = 7777)
        commandRunner.verifyAdbCommandsForDevice(deviceId = DEVICE_SAMSUNG.deviceId, tcpIpPort = 7778)
    }

    @Test
    fun handlesDeviceRemoved() {
        every { devicesService.devices() } returns mapOf(
            DEVICE_PIXEL.deviceId to Device(
                DEVICE_PIXEL.deviceId,
                DEVICE_PIXEL.model,
                DEVICE_PIXEL.manufacturer,
                DEVICE_PIXEL.androidVersion,
                DEVICE_PIXEL.apiLevel,
                DEVICE_PIXEL.port
            )
        )
        every { devicesService.delete(DEVICE_PIXEL.deviceId) } just Runs

        adbMonitor.refreshDevicesWith(listOf())

        verify { devicesService.devices() }
        verify { devicesService.delete(DEVICE_PIXEL.deviceId) }
    }

    @Test
    fun handlesDeviceRemovedAndKeepsExisting() {
        every { devicesService.devices() } returns mapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        every { devicesService.delete(DEVICE_PIXEL.deviceId) } just Runs

        adbMonitor.refreshDevicesWith(listOf(DEVICE_SAMSUNG.deviceId))

        verify { devicesService.devices() }
        verify { devicesService.delete(DEVICE_PIXEL.deviceId) }
    }


}


fun CommandRunner.mockRestartAdb() {
    val commandRunner = this
    every { commandRunner.exec("adb kill-server") } returns ADB_KILL_SERVER.output
    every { commandRunner.exec("adb start-server") } returns ADB_START_SERVER.output
}

fun CommandRunner.verifyRestartAdb() {
    val commandRunner = this
    verify { commandRunner.exec("adb start-server") }
    verify { commandRunner.exec("adb kill-server") }
}

fun CommandRunner.mockAdbCommandsForDevice(
    deviceId: DeviceId,
    tcpIpPort: Int,
    adbModelResponse: AdbOutput,
    adbManufacturerResponse: AdbOutput,
    adbAndroidVersionResponse: AdbOutput,
    adbApiLevelResponse: AdbOutput
) {
    val commandRunner = this
    every { commandRunner.exec("adb -s ${deviceId.id} tcpip 5555") } returns ADB_TCP_IP.output
    every { commandRunner.exec("adb -s ${deviceId.id} wait-for-device") } returns ADB_WAIT_FOR_DEVICE.output
    every { commandRunner.exec("adb -s ${deviceId.id} forward tcp:$tcpIpPort tcp:5555") } returns AdbOutput.ADB_FORWARD_IP.output
    every { commandRunner.exec("adb -s ${deviceId.id} connect 127.0.0.1:$tcpIpPort") } returns AdbOutput.ADB_CONNECT_SUCCESS.output
    every { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.product.model") } returns adbModelResponse.output
    every { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.product.manufacturer") } returns adbManufacturerResponse.output
    every { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.build.version.release") } returns adbAndroidVersionResponse.output
    every { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.build.version.sdk") } returns adbApiLevelResponse.output
}

fun CommandRunner.verifyAdbCommandsForDevice(deviceId: DeviceId, tcpIpPort: Int) {
    val commandRunner = this
    verify { commandRunner.exec("adb -s ${deviceId.id} tcpip 5555") }
    verify { commandRunner.exec("adb -s ${deviceId.id} wait-for-device") }
    verify { commandRunner.exec("adb -s ${deviceId.id} forward tcp:$tcpIpPort tcp:5555") }
    verify { commandRunner.exec("adb -s ${deviceId.id} connect 127.0.0.1:$tcpIpPort") }
    verify { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.product.model") }
    verify { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.product.manufacturer") }
    verify { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.build.version.release") }
    verify { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.build.version.sdk") }
}
