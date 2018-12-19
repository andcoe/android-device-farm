package org.andcoe.adf.core

class AdbMonitor(private val adb: Adb) {

    var localPortCounter = 7777
    var preparedDevices = mutableListOf<String>()

    private companion object {
        const val DEVICE_PORT = 5555
        const val SCAN_RETRY_EVERY = 5_000L
    }

    fun startScanning() {
        restartAdb()
        while (true) {
            println("=========================================================================")
            println("============================== scanDevices ==============================")
            println("=========================================================================")
            scanDevices()
            println("AdbMonitor => scheduling another scan in ${SCAN_RETRY_EVERY}ms")
            Thread.sleep(SCAN_RETRY_EVERY)
        }
    }

    fun restartAdb() {
        adb.killServer()
        adb.startServer()
    }

    fun scanDevices() {
        val devices = adb.devices()
        println("AdbMonitor => connected devices: $devices")

        val newDevices = difference(devices, preparedDevices)
        println("AdbMonitor => connected now: $newDevices")

        val removedDevices = difference(preparedDevices, devices)
        println("AdbMonitor => removed now: $removedDevices")

        val newPreparedDevices = difference(preparedDevices, removedDevices)
        preparedDevices = newPreparedDevices.toMutableList()

        newDevices.forEach {
            setupDevice(it)
            preparedDevices.add(it)
        }

        println("AdbMonitor => prepared devices: $preparedDevices")
    }

    private fun setupDevice(deviceId: String) {
        val localPort = localPortCounter++
        adb.tcpIpFor(deviceId)
        adb.forwardFor(deviceId, localPort, DEVICE_PORT)
        adb.connect(deviceId, localPort)
        adb.deviceModelFor(deviceId)
        adb.deviceManufacturerFor(deviceId)
    }

    private fun <T> difference(list1: List<T>, list2: List<T>): List<T> {
        val newList = mutableListOf<T>()
        list1.forEach { if (!list2.contains(it)) newList.add(it) }
        return newList
    }

}