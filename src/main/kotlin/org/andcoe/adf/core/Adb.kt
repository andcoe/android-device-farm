package org.andcoe.adf.core

import org.andcoe.adf.devices.DeviceId

class Adb(private val commandRunner: CommandRunner) {

    private companion object {
        const val LOCAL_IP = "127.0.0.1"
    }

    fun killServer() {
        commandRunner.exec("adb kill-server")
    }

    fun startServer() {
        commandRunner.exec("adb start-server")
    }

    fun waitForDevice(deviceId: String) {
        commandRunner.exec("adb -s $deviceId wait-for-device")
    }

    fun devices(): List<DeviceId> =
        commandRunner.exec("adb devices")
            .replace("List of devices attached\n", "")
            .replace("device", "")
            .split("\n")
            .map { it.trim() }
            .filterNot { it.isEmpty() }
            .filterNot { it.startsWith(LOCAL_IP) }
            .map { DeviceId(it) }

    fun deviceModelFor(deviceId: String): String =
        commandRunner.exec("adb -s $deviceId shell getprop ro.product.model").trim()


    fun deviceManufacturerFor(deviceId: String): String =
        commandRunner.exec("adb -s $deviceId shell getprop ro.product.manufacturer")
            .trim()

    fun tcpIpFor(deviceId: String) {
        commandRunner.exec("adb -s $deviceId tcpip 5555")
        waitForDevice(deviceId)
    }

    fun forwardFor(deviceId: String, localPort: Int, devicePort: Int) {
        commandRunner.exec("adb -s $deviceId forward tcp:$localPort tcp:$devicePort")
    }

    fun connect(deviceId: String, port: Int) {
        commandRunner.exec("adb -s $deviceId connect $LOCAL_IP:$port")
    }

}