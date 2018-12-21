package org.andcoe.adf

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.andcoe.adf.core.Adb
import org.andcoe.adf.core.AdbMonitor
import org.andcoe.adf.core.CommandRunner
import org.andcoe.adf.devices.DevicesDao
import org.andcoe.adf.devices.DevicesResource
import org.andcoe.adf.devices.DevicesService
import org.andcoe.adf.leases.LeasesDao
import org.andcoe.adf.leases.LeasesResource
import org.andcoe.adf.leases.LeasesService
import java.util.concurrent.Executors

//Aliased main function used to start the app and fake commands run on terminal
fun main(args: Array<String>) = main(args, CommandRunner())

fun main(args: Array<String>, commandRunner: CommandRunner) {
    val deviceDao = DevicesDao()
    val deviceService = DevicesService(deviceDao)
    val deviceResource = DevicesResource(deviceService)

    val leaseDao = LeasesDao()
    val leaseService = LeasesService(deviceService, leaseDao)
    val leasesResource = LeasesResource(leaseService)

    val adb = Adb(commandRunner)
    val adbMonitor = AdbMonitor(deviceService, adb)
    Executors.newSingleThreadExecutor().execute { adbMonitor.startScanning() }

    embeddedServer(
        factory = Netty,
        port = 8000,
        module = AppModule(
            devicesResource = deviceResource,
            leasesResource = leasesResource
        ).module()
    ).start(wait = true)
}