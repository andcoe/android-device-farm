package org.andcoe.adf.core

import org.andcoe.adf.devices.DeviceId
import org.andcoe.adf.devices.DevicesService

class AdbMonitor(
    private val devicesService: DevicesService,
    private val adb: Adb
) {

    var localPortCounter = 7777

    private companion object {
        const val DEVICE_PORT = 5555
        const val SCAN_RETRY_EVERY = 2_000L
    }

    fun startScanning() {
        restartAdb()
        while (true) {
            println("=========================================================================")
            println("============================== scanDevices ==============================")
            println("=========================================================================")
            val connectedDevices = adb.devices()
            refreshDevicesWith(connectedDevices)
            println("AdbMonitor => scheduling another scan in ${SCAN_RETRY_EVERY}ms")
            Thread.sleep(SCAN_RETRY_EVERY)
        }
    }

    fun restartAdb() {
        adb.killServer()
        adb.startServer()
    }

    fun refreshDevicesWith(connectedDevices: List<DeviceId>) {
        println("AdbMonitor => connected devices: $connectedDevices")

        val preparedDevices = devicesService.devices()

        val removedDevices: List<DeviceId> = preparedDevices.keys.filter { !connectedDevices.contains(it) }
        println("AdbMonitor => removed now: $removedDevices")

        preparedDevices
            .filter { removedDevices.contains(it.key) }
            .map { devicesService.delete(it.key) }

        val newDevices: List<DeviceId> = connectedDevices.filter { !preparedDevices.containsKey(it) }
        println("AdbMonitor => connected now: $newDevices")

        newDevices.forEach {
            val adbDevice = setupDevice(it.id)
            devicesService.create(adbDevice)
        }

        println("AdbMonitor => prepared devices: ${devicesService.devices().map { it.key }}")
    }

    private fun setupDevice(deviceId: String): AdbDevice {
        val localPort = localPortCounter++
        adb.tcpIpFor(deviceId)
        adb.forwardFor(deviceId, localPort, DEVICE_PORT)
        adb.connect(deviceId, localPort)

        val model = adb.modelFor(deviceId)
        val manufacturer = adb.manufacturerFor(deviceId)
        val androidVersion = adb.androidVersionFor(deviceId)
        val apiLevel = adb.apiLevelFor(deviceId)
        return AdbDevice(deviceId, model, manufacturer, androidVersion, apiLevel, localPort.toString())
    }

}