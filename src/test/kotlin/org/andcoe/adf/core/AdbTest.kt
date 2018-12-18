package org.andcoe.adf.core

import io.mockk.Runs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

class AdbTest {

    private companion object {
        const val adbDevicesOutput = """
List of devices attached
127.0.0.1:7778	device
127.0.0.1:7777	device
XM043220	device
3204486bc15611b5	device


"""
    }

    class MockedCommandRunner(private val output: String) : CommandRunner {
        override fun exec(command: String, workingDir: File, timeoutAmount: Long, timeoutUnit: TimeUnit): String {
            return output
        }
    }

    @Test
    fun returnsDeviceIds() {
        val adb = Adb(MockedCommandRunner(adbDevicesOutput))
        val result = adb.devices()
        assertThat(result).isEqualTo(listOf("XM043220", "3204486bc15611b5"))
    }


    @Test
    fun connectsToDevice() {
        val commandRunner : CommandRunner = mockk()
        every {commandRunner.exec("adb -s PIXEL connect 127.0.0.1:1234", any(),any(),any())} returns "asdfasf"


        val adb = Adb(commandRunner)

        val result = adb.connect("PIXEL", 1234)
        verify {commandRunner.exec("adb -s PIXEL connect localhost:1234")}
        //assertThat(result).isEqualTo(listOf("XM043220", "3204486bc15611b5"))
    }
}