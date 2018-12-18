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
        const val waitForDeviceOutput = """"""


        const val adbConnectSuccess =
                """connected to 127.0.0.1:7777

"""
        const val adbForwardListOne =
                """FA79L1A04130 tcp:7777 tcp:5555

"""
    }

    @Test
    fun executesWaitForDevice() {
        val commandRunner: CommandRunner = mockk()
        every { commandRunner.exec("adb -s XM043220 wait-for-device") } returns waitForDeviceOutput
        val adb = Adb(commandRunner)
        adb.waitForDevice("XM043220")
        verify { commandRunner.exec("adb -s XM043220 wait-for-device") }
    }

    @Test
    fun returnsDeviceIds() {
        val commandRunner: CommandRunner = mockk()
        every { commandRunner.exec("adb devices") } returns devicesOutput
        val adb = Adb(commandRunner)
        val result = adb.devices()
        assertThat(result).isEqualTo(listOf("XM043220", "3204486bc15611b5"))
    }

    @Test
    fun connectsToDevice() {
        val commandRunner : CommandRunner = mockk()
        every { commandRunner.exec("adb -s PIXEL connect 127.0.0.1:1234", any(), any(), any()) } returns adbConnectSuccess
        val adb = Adb(commandRunner)

        adb.connect("PIXEL", 1234)
        verify { commandRunner.exec("adb -s PIXEL connect 127.0.0.1:1234") }
    }

    @Test
    fun forwardFor() {
        val commandRunner : CommandRunner = mockk()
        every {commandRunner.exec("adb -s FA79L1A04130 forward tcp:7777 tcp:5555", any(),any(),any())} returns adbConnectSuccess
        val adb = Adb(commandRunner)
        val result = adb.forwardFor("FA79L1A04130", 7777, 5555)
        assertThat(result)
        verify {commandRunner.exec("adb -s FA79L1A04130 forward tcp:7777 tcp:5555")}
    }
}