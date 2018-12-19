package org.andcoe.adf

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.andcoe.adf.core.Adb
import org.andcoe.adf.core.AdbMonitor
import org.andcoe.adf.core.CommandRunner
import org.andcoe.adf.devices.DeviceDao
import org.andcoe.adf.devices.DeviceResource
import org.andcoe.adf.devices.DeviceService
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    val deviceDao = DeviceDao()
    val deviceService = DeviceService(deviceDao)
    val deviceResource = DeviceResource(deviceService)

    val commandRunner = CommandRunner()
    val adb = Adb(commandRunner)
    val adbMonitor = AdbMonitor(deviceService, adb)
    Executors.newSingleThreadExecutor().execute { adbMonitor.startScanning() }

    embeddedServer(
        factory = Netty,
        port = 8000,
        module = AppModule(deviceResource = deviceResource).module()
    )
    .start(wait = true)
}