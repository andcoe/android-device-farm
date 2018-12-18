package org.andcoe.adf.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.AdbOutput.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AdbTest {

    private val commandRunner: CommandRunner = mockk()

    @Test
    fun killsServer() {
        every { commandRunner.exec("adb kill-server") } returns ""
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
        every { commandRunner.exec("adb -s XM043220 wait-for-device") } returns ""
        val adb = Adb(commandRunner)
        adb.waitForDevice("XM043220")
        verify { commandRunner.exec("adb -s XM043220 wait-for-device") }
    }

    @Test
    fun returnsDeviceIds() {
        every { commandRunner.exec("adb devices") } returns ADB_DEVICES.output
        val adb = Adb(commandRunner)
        val result = adb.devices()
        assertThat(result).isEqualTo(listOf("XM043220", "3204486bc15611b5"))
        verify { commandRunner.exec("adb devices") }
    }

    @Test
    fun returnsDeviceModel() {
        every { commandRunner.exec("adb -s XM043220 shell getprop ro.product.model") } returns ADB_DEVICE_MODEL.output
        val adb = Adb(commandRunner)
        val result = adb.deviceModelFor("XM043220")
        assertThat(result).isEqualTo("Aquaris X5 Plus")
        verify { commandRunner.exec("adb -s XM043220 shell getprop ro.product.model") }
    }

    @Test
    fun returnsDeviceManufacturerFor() {
        every { commandRunner.exec("adb -s XM043220 shell getprop ro.product.manufacturer") } returns ADB_DEVICE_MANUFACTURER.output
        val adb = Adb(commandRunner)
        val result = adb.deviceManufacturerFor("XM043220")
        assertThat(result).isEqualTo("bq")
        verify { commandRunner.exec("adb -s XM043220 shell getprop ro.product.manufacturer") }
    }

    @Test
    fun connectsToDevice() {
        every { commandRunner.exec("adb -s PIXEL connect 127.0.0.1:1234") } returns ADB_CONNECT_SUCCESS.output
        val adb = Adb(commandRunner)

        adb.connect("PIXEL", 1234)
        verify { commandRunner.exec("adb -s PIXEL connect 127.0.0.1:1234") }
    }

    @Test
    fun forwardFor() {
        every { commandRunner.exec("adb -s FA79L1A04130 forward tcp:7777 tcp:5555") } returns ""
        val adb = Adb(commandRunner)
        val result = adb.forwardFor("FA79L1A04130", 7777, 5555)
        assertThat(result)
        verify { commandRunner.exec("adb -s FA79L1A04130 forward tcp:7777 tcp:5555") }
    }
}