package org.andcoe.adf.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.andcoe.adf.devices.DeviceId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.AdbOutput.*

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
        every { commandRunner.exec("adb -s PIXEL wait-for-device") } returns ADB_WAIT_FOR_DEVICE.output
        val adb = Adb(commandRunner)
        adb.waitForDevice("PIXEL")
        verify { commandRunner.exec("adb -s PIXEL wait-for-device") }
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
        assertThat(result).isEqualTo(listOf(DeviceId("PIXEL"), DeviceId("SAMSUNG")))
        verify { commandRunner.exec("adb devices") }
    }

    @Test
    fun returnsDeviceModel() {
        every { commandRunner.exec("adb -s PIXEL shell getprop ro.product.model") } returns ADB_DEVICE_MODEL.output
        val adb = Adb(commandRunner)
        val result = adb.deviceModelFor("PIXEL")
        assertThat(result).isEqualTo("Aquaris X5 Plus")
        verify { commandRunner.exec("adb -s PIXEL shell getprop ro.product.model") }
    }

    @Test
    fun returnsDeviceManufacturer() {
        every { commandRunner.exec("adb -s PIXEL shell getprop ro.product.manufacturer") } returns ADB_DEVICE_MANUFACTURER.output
        val adb = Adb(commandRunner)
        val result = adb.deviceManufacturerFor("PIXEL")
        assertThat(result).isEqualTo("bq")
        verify { commandRunner.exec("adb -s PIXEL shell getprop ro.product.manufacturer") }
    }

    @Test
    fun setsTcpIpForDeviceAndWaitsForItToBecomeReady() {
        every { commandRunner.exec("adb -s PIXEL tcpip 5555") } returns ADB_TCP_IP.output
        every { commandRunner.exec("adb -s PIXEL wait-for-device") } returns ADB_WAIT_FOR_DEVICE.output
        val adb = Adb(commandRunner)
        val result = adb.tcpIpFor("PIXEL")
        assertThat(result)
        verify { commandRunner.exec("adb -s PIXEL tcpip 5555") }
        verify { commandRunner.exec("adb -s PIXEL wait-for-device") }
    }

    @Test
    fun setsForwardForDevice() {
        every { commandRunner.exec("adb -s FA79L1A04130 forward tcp:7777 tcp:5555") } returns ADB_FORWARD_IP.output
        val adb = Adb(commandRunner)
        val result = adb.forwardFor("FA79L1A04130", 7777, 5555)
        assertThat(result)
        verify { commandRunner.exec("adb -s FA79L1A04130 forward tcp:7777 tcp:5555") }
    }

    @Test
    fun connectsToDevice() {
        every { commandRunner.exec("adb -s PIXEL connect 127.0.0.1:1234") } returns ADB_CONNECT_SUCCESS.output
        val adb = Adb(commandRunner)
        adb.connect("PIXEL", 1234)
        verify { commandRunner.exec("adb -s PIXEL connect 127.0.0.1:1234") }
    }
}