package org.andcoe.adf.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.andcoe.adf.devices.DeviceId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.AdbOutput.*
import util.DeviceUtils.Companion.ADB_PIXEL
import util.DeviceUtils.Companion.ADB_SAMSUNG

class AdbTest {

    private val commandRunner: CommandRunner = mockk()

    @Test
    fun killsServer() {
        every { commandRunner.exec("adb kill-server") } returns ADB_KILL_SERVER.output
        val adb = Adb(commandRunner)
        adb.killServer()
        verify { commandRunner.exec("adb kill-server") }
    }

    @Test
    fun startsServer() {
        every { commandRunner.exec("adb start-server") } returns ADB_START_SERVER.output
        val adb = Adb(commandRunner)
        adb.startServer()
        verify { commandRunner.exec("adb start-server") }
    }

    @Test
    fun waitsForDevice() {
        every { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} wait-for-device") } returns ADB_WAIT_FOR_DEVICE.output
        val adb = Adb(commandRunner)
        adb.waitForDevice(ADB_PIXEL.deviceId)
        verify { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} wait-for-device") }
    }

    @Test
    fun devicesHandlesNoDevices() {
        every { commandRunner.exec("adb devices") } returns ADB_DEVICES_EMPTY.output
        val adb = Adb(commandRunner)
        val result = adb.devices()
        assertThat(result).isEmpty()
        verify { commandRunner.exec("adb devices") }
    }

    @Test
    fun returnsDeviceIds() {
        every { commandRunner.exec("adb devices") } returns ADB_DEVICES.output
        val adb = Adb(commandRunner)
        val result = adb.devices()
        assertThat(result).isEqualTo(listOf(DeviceId(ADB_PIXEL.deviceId), DeviceId(ADB_SAMSUNG.deviceId)))
        verify { commandRunner.exec("adb devices") }
    }

    @Test
    fun returnsDeviceModel() {
        every { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} shell getprop ro.product.model") } returns ADB_DEVICE_MODEL_PIXEL.output
        val adb = Adb(commandRunner)
        val result = adb.deviceModelFor(ADB_PIXEL.deviceId)
        assertThat(result).isEqualTo(ADB_PIXEL.model)
        verify { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} shell getprop ro.product.model") }
    }

    @Test
    fun returnsDeviceManufacturer() {
        every { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} shell getprop ro.product.manufacturer") } returns ADB_DEVICE_MANUFACTURER_GOOGLE.output
        val adb = Adb(commandRunner)
        val result = adb.deviceManufacturerFor(ADB_PIXEL.deviceId)
        assertThat(result).isEqualTo(ADB_PIXEL.manufacturer)
        verify { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} shell getprop ro.product.manufacturer") }
    }

    @Test
    fun setsTcpIpForDeviceAndWaitsForItToBecomeReady() {
        every { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} tcpip 5555") } returns ADB_TCP_IP.output
        every { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} wait-for-device") } returns ADB_WAIT_FOR_DEVICE.output
        val adb = Adb(commandRunner)
        val result = adb.tcpIpFor(ADB_PIXEL.deviceId)
        assertThat(result)
        verify { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} tcpip 5555") }
        verify { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} wait-for-device") }
    }

    @Test
    fun setsForwardForDevice() {
        every { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} forward tcp:7777 tcp:5555") } returns ADB_FORWARD_IP.output
        val adb = Adb(commandRunner)
        val result = adb.forwardFor(ADB_PIXEL.deviceId, 7777, 5555)
        assertThat(result)
        verify { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} forward tcp:7777 tcp:5555") }
    }

    @Test
    fun connectsToDevice() {
        every { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} connect 127.0.0.1:1234") } returns ADB_CONNECT_SUCCESS.output
        val adb = Adb(commandRunner)
        adb.connect(ADB_PIXEL.deviceId, 1234)
        verify { commandRunner.exec("adb -s ${ADB_PIXEL.deviceId} connect 127.0.0.1:1234") }
    }
}