package org.andcoe.adf

import org.andcoe.adf.core.Adb
import org.andcoe.adf.core.AdbMonitor
import org.andcoe.adf.core.CommandRunner

fun main(args: Array<String>) {
    val adb = Adb(CommandRunner())
    val adbMonitor = AdbMonitor(adb)

    adb.devices().forEach { adbMonitor.setupDevice(it) }
}

