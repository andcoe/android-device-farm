package org.andcoe.adf.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import util.AdbOutput
import util.AdbOutput.ADB_TCP_IP
import util.AdbOutput.ADB_WAIT_FOR_DEVICE

class AdbMonitorTest {

    private val commandRunner: CommandRunner = mockk()

    @Test
    fun restartsAdb() {
        val adbMonitor = AdbMonitor(Adb(commandRunner))
        every { commandRunner.exec("adb kill-server") } returns AdbOutput.ADB_KILL_SERVER.output
        every { commandRunner.exec("adb start-server") } returns AdbOutput.ADB_START_SERVER.output

        adbMonitor.restartAdb()

        verify { commandRunner.exec("adb start-server") }
        verify { commandRunner.exec("adb kill-server") }
    }

    @Test
    fun scansDevices() {
        val adbMonitor = AdbMonitor(Adb(commandRunner))

        every { commandRunner.exec("adb devices") } returns AdbOutput.ADB_DEVICES.output

        //setup PIXEL
        every { commandRunner.exec("adb -s PIXEL tcpip 5555") } returns ADB_TCP_IP.output
        every { commandRunner.exec("adb -s PIXEL wait-for-device") } returns ADB_WAIT_FOR_DEVICE.output
        every { commandRunner.exec("adb -s PIXEL forward tcp:7777 tcp:5555") } returns AdbOutput.ADB_FORWARD_IP.output
        every { commandRunner.exec("adb -s PIXEL connect 127.0.0.1:7777") } returns AdbOutput.ADB_CONNECT_SUCCESS.output
        every { commandRunner.exec("adb -s PIXEL shell getprop ro.product.model") } returns AdbOutput.ADB_DEVICE_MODEL.output
        every { commandRunner.exec("adb -s PIXEL shell getprop ro.product.manufacturer") } returns AdbOutput.ADB_DEVICE_MANUFACTURER.output

        //setup SAMSUNG
        every { commandRunner.exec("adb -s SAMSUNG tcpip 5555") } returns ADB_TCP_IP.output
        every { commandRunner.exec("adb -s SAMSUNG wait-for-device") } returns ADB_WAIT_FOR_DEVICE.output
        every { commandRunner.exec("adb -s SAMSUNG forward tcp:7778 tcp:5555") } returns AdbOutput.ADB_FORWARD_IP.output
        every { commandRunner.exec("adb -s SAMSUNG connect 127.0.0.1:7778") } returns AdbOutput.ADB_CONNECT_SUCCESS.output
        every { commandRunner.exec("adb -s SAMSUNG shell getprop ro.product.model") } returns AdbOutput.ADB_DEVICE_MODEL.output
        every { commandRunner.exec("adb -s SAMSUNG shell getprop ro.product.manufacturer") } returns AdbOutput.ADB_DEVICE_MANUFACTURER.output

        adbMonitor.scanDevices()

        verify { commandRunner.exec("adb devices") }

        //verify PIXEL
        verify { commandRunner.exec("adb -s PIXEL tcpip 5555") }
        verify { commandRunner.exec("adb -s PIXEL wait-for-device") }
        verify { commandRunner.exec("adb -s PIXEL forward tcp:7777 tcp:5555") }
        verify { commandRunner.exec("adb -s PIXEL connect 127.0.0.1:7777") }
        verify { commandRunner.exec("adb -s PIXEL shell getprop ro.product.model") }
        verify { commandRunner.exec("adb -s PIXEL shell getprop ro.product.manufacturer") }

        //verify SAMSUNG
        verify { commandRunner.exec("adb -s SAMSUNG tcpip 5555") }
        verify { commandRunner.exec("adb -s SAMSUNG wait-for-device") }
        verify { commandRunner.exec("adb -s SAMSUNG forward tcp:7778 tcp:5555") }
        verify { commandRunner.exec("adb -s SAMSUNG connect 127.0.0.1:7778") }
        verify { commandRunner.exec("adb -s SAMSUNG shell getprop ro.product.model") }
        verify { commandRunner.exec("adb -s SAMSUNG shell getprop ro.product.manufacturer") }
    }
}