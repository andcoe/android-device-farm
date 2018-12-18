package org.andcoe.adf.core

class AdbMonitor(private val adb: Adb) {

    var localPortCounter = 7777

    private companion object {
        const val DEVICE_PORT = 5555
    }

    fun setupDevice(deviceId: String) {
        val localPort = localPortCounter++
        adb.tcpIpFor(deviceId)
        adb.forwardFor(deviceId, localPort, DEVICE_PORT)
        adb.connect(deviceId, localPort)
        adb.deviceModelFor(deviceId)
        adb.deviceManufacturerFor(deviceId)
    }


}