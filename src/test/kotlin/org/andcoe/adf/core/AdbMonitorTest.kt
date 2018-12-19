package org.andcoe.adf.core

import io.mockk.*
import org.andcoe.adf.devices.Device
import org.andcoe.adf.devices.DeviceId
import org.andcoe.adf.devices.DeviceService
import org.junit.Test
import util.AdbOutput
import util.AdbOutput.ADB_TCP_IP
import util.AdbOutput.ADB_WAIT_FOR_DEVICE
import util.DeviceUtils.Companion.ADB_PIXEL
import util.DeviceUtils.Companion.ADB_SAMSUNG
import util.DeviceUtils.Companion.DEVICE_PIXEL
import util.DeviceUtils.Companion.DEVICE_SAMSUNG

class AdbMonitorTest {

    private val commandRunner: CommandRunner = mockk()
    private val deviceService: DeviceService = mockk()
    private val adbMonitor = AdbMonitor(
        deviceService = deviceService,
        adb = Adb(commandRunner)
    )

    @Test
    fun restartsAdbAndRunsCommands() {
        every { commandRunner.exec("adb kill-server") } returns AdbOutput.ADB_KILL_SERVER.output
        every { commandRunner.exec("adb start-server") } returns AdbOutput.ADB_START_SERVER.output

        adbMonitor.restartAdb()

        verify { commandRunner.exec("adb start-server") }
        verify { commandRunner.exec("adb kill-server") }
    }

    @Test
    fun handlesNoDevices() {
        every { deviceService.devices() } returns emptyMap()
        adbMonitor.refreshDevicesWith(listOf())
        verify { deviceService.devices() }
    }

    @Test
    fun handlesNewDeviceConnected() {
        mockAdbCommandsForDevice(
            deviceId = DEVICE_PIXEL.deviceId,
            tcpIpPort = 7777,
            adbModelResponse = AdbOutput.ADB_DEVICE_MODEL_PIXEL,
            adbManufacturerResponse = AdbOutput.ADB_DEVICE_MANUFACTURER_GOOGLE
        )
        every { deviceService.devices() } returns emptyMap()
        every { deviceService.create(ADB_PIXEL) } returns DEVICE_PIXEL

        adbMonitor.refreshDevicesWith(listOf(DEVICE_PIXEL.deviceId))

        verify { deviceService.devices() }
        verify(exactly = 1) { deviceService.create(ADB_PIXEL) }
        verifyAdbCommandsForDevice(deviceId = DEVICE_PIXEL.deviceId, tcpIpPort = 7777)
    }

    @Test
    fun handlesMultipleNewDevicesConnected() {
        mockAdbCommandsForDevice(
            deviceId = DEVICE_PIXEL.deviceId,
            tcpIpPort = 7777,
            adbModelResponse = AdbOutput.ADB_DEVICE_MODEL_PIXEL,
            adbManufacturerResponse = AdbOutput.ADB_DEVICE_MANUFACTURER_GOOGLE
        )
        mockAdbCommandsForDevice(
            deviceId = DEVICE_SAMSUNG.deviceId,
            tcpIpPort = 7778,
            adbModelResponse = AdbOutput.ADB_DEVICE_MODEL_S9,
            adbManufacturerResponse = AdbOutput.ADB_DEVICE_MANUFACTURER_SAMSUNG
        )
        every { deviceService.devices() } returns emptyMap()
        every { deviceService.create(ADB_PIXEL) } returns DEVICE_PIXEL
        every { deviceService.create(ADB_SAMSUNG) } returns DEVICE_SAMSUNG

        adbMonitor.refreshDevicesWith(listOf(DEVICE_PIXEL.deviceId, DEVICE_SAMSUNG.deviceId))

        verify { deviceService.devices() }
        verify(exactly = 1) { deviceService.create(ADB_PIXEL) }
        verify(exactly = 1) { deviceService.create(ADB_SAMSUNG) }
        verifyAdbCommandsForDevice(deviceId = DEVICE_PIXEL.deviceId, tcpIpPort = 7777)
        verifyAdbCommandsForDevice(deviceId = DEVICE_SAMSUNG.deviceId, tcpIpPort = 7778)
    }

    @Test
    fun handlesDeviceRemoved() {
        every { deviceService.devices() } returns mapOf(
            DEVICE_PIXEL.deviceId to Device(
                DEVICE_PIXEL.deviceId,
                DEVICE_PIXEL.model,
                DEVICE_PIXEL.manufacturer,
                DEVICE_PIXEL.port
            )
        )
        every { deviceService.remove(DEVICE_PIXEL.deviceId) } just Runs

        adbMonitor.refreshDevicesWith(listOf())

        verify { deviceService.devices() }
        verify { deviceService.remove(DEVICE_PIXEL.deviceId) }
    }

    @Test
    fun handlesDeviceRemovedAndKeepsExisting() {
        every { deviceService.devices() } returns mapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        every { deviceService.remove(DEVICE_PIXEL.deviceId) } just Runs

        adbMonitor.refreshDevicesWith(listOf(DEVICE_SAMSUNG.deviceId))

        verify { deviceService.devices() }
        verify { deviceService.remove(DEVICE_PIXEL.deviceId) }
    }

    private fun mockAdbCommandsForDevice(
        deviceId: DeviceId,
        tcpIpPort: Int,
        adbModelResponse: AdbOutput,
        adbManufacturerResponse: AdbOutput
    ) {
        every { commandRunner.exec("adb -s ${deviceId.id} tcpip 5555") } returns ADB_TCP_IP.output
        every { commandRunner.exec("adb -s ${deviceId.id} wait-for-device") } returns ADB_WAIT_FOR_DEVICE.output
        every { commandRunner.exec("adb -s ${deviceId.id} forward tcp:$tcpIpPort tcp:5555") } returns AdbOutput.ADB_FORWARD_IP.output
        every { commandRunner.exec("adb -s ${deviceId.id} connect 127.0.0.1:$tcpIpPort") } returns AdbOutput.ADB_CONNECT_SUCCESS.output
        every { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.product.model") } returns adbModelResponse.output
        every { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.product.manufacturer") } returns adbManufacturerResponse.output
    }

    private fun verifyAdbCommandsForDevice(deviceId: DeviceId, tcpIpPort: Int) {
        verify { commandRunner.exec("adb -s ${deviceId.id} tcpip 5555") }
        verify { commandRunner.exec("adb -s ${deviceId.id} wait-for-device") }
        verify { commandRunner.exec("adb -s ${deviceId.id} forward tcp:$tcpIpPort tcp:5555") }
        verify { commandRunner.exec("adb -s ${deviceId.id} connect 127.0.0.1:$tcpIpPort") }
        verify { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.product.model") }
        verify { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.product.manufacturer") }
    }
}