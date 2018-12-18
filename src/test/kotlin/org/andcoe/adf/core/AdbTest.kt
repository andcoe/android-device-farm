package org.andcoe.adf.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AdbTest {

    private companion object {

        const val devicesOutput = """
List of devices attached
127.0.0.1:7778	device
127.0.0.1:7777	device
XM043220	device
3204486bc15611b5	device


"""

        const val adbConnectSuccess = """
            connected to 127.0.0.1:7777

"""

        const val adbStartServer = """
* daemon not running; starting now at tcp:5037
* daemon started successfully
"""
    }

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
        every { commandRunner.exec("adb start-server") } returns adbStartServer
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
        every { commandRunner.exec("adb devices") } returns devicesOutput
        val adb = Adb(commandRunner)
        val result = adb.devices()
        assertThat(result).isEqualTo(listOf("XM043220", "3204486bc15611b5"))
    }

    @Test
    fun connectsToDevice() {
        every { commandRunner.exec("adb -s PIXEL connect 127.0.0.1:1234") } returns adbConnectSuccess
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