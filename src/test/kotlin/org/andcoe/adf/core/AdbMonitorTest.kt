package org.andcoe.adf.core

import io.mockk.*
import org.andcoe.adf.devices.Device
import org.andcoe.adf.devices.DeviceId
import org.andcoe.adf.devices.DeviceService
import org.junit.Test
import util.AdbOutput
import util.AdbOutput.ADB_TCP_IP
import util.AdbOutput.ADB_WAIT_FOR_DEVICE

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
        mockAdbCommandsForDevice(deviceId = DeviceId("PIXEL"), tcpIpPort = 7777)
        every { deviceService.devices() } returns emptyMap()
        every { deviceService.createDevice(DeviceId("PIXEL")) } returns Device(DeviceId("PIXEL"))

        adbMonitor.refreshDevicesWith(listOf(DeviceId("PIXEL")))

        verify { deviceService.devices() }
        verify(exactly = 1) { deviceService.createDevice(DeviceId("PIXEL")) }
        verifyAdbCommandsForDevice(deviceId = DeviceId("PIXEL"), tcpIpPort = 7777)
    }

    @Test
    fun handlesMultipleNewDevicesConnected() {
        mockAdbCommandsForDevice(deviceId = DeviceId("PIXEL"), tcpIpPort = 7777)
        mockAdbCommandsForDevice(deviceId = DeviceId("SAMSUNG"), tcpIpPort = 7778)
        every { deviceService.devices() } returns emptyMap()
        every { deviceService.createDevice(DeviceId("PIXEL")) } returns Device(DeviceId("PIXEL"))
        every { deviceService.createDevice(DeviceId("SAMSUNG")) } returns Device(DeviceId("SAMSUNG"))

        adbMonitor.refreshDevicesWith(listOf(DeviceId("PIXEL"), DeviceId("SAMSUNG")))

        verify { deviceService.devices() }
        verify(exactly = 1) { deviceService.createDevice(DeviceId("PIXEL")) }
        verify(exactly = 1) { deviceService.createDevice(DeviceId("SAMSUNG")) }
        verifyAdbCommandsForDevice(deviceId = DeviceId("PIXEL"), tcpIpPort = 7777)
        verifyAdbCommandsForDevice(deviceId = DeviceId("SAMSUNG"), tcpIpPort = 7778)
    }

    @Test
    fun handlesDeviceRemoved() {
        every { deviceService.devices() } returns mapOf(DeviceId("PIXEL") to Device(DeviceId("PIXEL")))
        every { deviceService.remove(DeviceId("PIXEL")) } just Runs

        adbMonitor.refreshDevicesWith(listOf())

        verify { deviceService.devices() }
        verify { deviceService.remove(DeviceId("PIXEL")) }
    }

    @Test
    fun handlesDeviceRemovedAndKeepsExisting() {
        every { deviceService.devices() } returns mapOf(
            DeviceId("PIXEL") to Device(DeviceId("PIXEL")),
            DeviceId("SAMSUNG") to Device(DeviceId("SAMSUNG"))
        )
        every { deviceService.remove(DeviceId("PIXEL")) } just Runs

        adbMonitor.refreshDevicesWith(listOf(DeviceId("SAMSUNG")))

        verify { deviceService.devices() }
        verify { deviceService.remove(DeviceId("PIXEL")) }
    }

    private fun mockAdbCommandsForDevice(deviceId: DeviceId, tcpIpPort: Int) {
        every { commandRunner.exec("adb -s ${deviceId.id} tcpip 5555") } returns ADB_TCP_IP.output
        every { commandRunner.exec("adb -s ${deviceId.id} wait-for-device") } returns ADB_WAIT_FOR_DEVICE.output
        every { commandRunner.exec("adb -s ${deviceId.id} forward tcp:$tcpIpPort tcp:5555") } returns AdbOutput.ADB_FORWARD_IP.output
        every { commandRunner.exec("adb -s ${deviceId.id} connect 127.0.0.1:$tcpIpPort") } returns AdbOutput.ADB_CONNECT_SUCCESS.output
        every { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.product.model") } returns AdbOutput.ADB_DEVICE_MODEL.output
        every { commandRunner.exec("adb -s ${deviceId.id} shell getprop ro.product.manufacturer") } returns AdbOutput.ADB_DEVICE_MANUFACTURER.output
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