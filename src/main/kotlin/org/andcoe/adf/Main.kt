package org.andcoe.adf

import org.andcoe.adf.core.Adb
import org.andcoe.adf.core.AdbMonitor
import org.andcoe.adf.core.CommandRunner
import org.andcoe.adf.devices.DeviceDao
import org.andcoe.adf.devices.DeviceService

fun main(args: Array<String>) {
    val deviceDao = DeviceDao()
    val deviceService = DeviceService(deviceDao)
    val adb = Adb(CommandRunner())
    val adbMonitor = AdbMonitor(deviceService, adb)

    adbMonitor.startScanning()
}

