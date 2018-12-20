package org.andcoe.adf.leases

import org.andcoe.adf.devices.Device
import org.andcoe.adf.devices.DeviceService

class LeaseService(private val deviceService: DeviceService,
                   private val leaseDao: LeaseDao) {

    fun create(): Lease? {
        val leases = leaseDao.leases()
        val devices = deviceService.devices()

        val device: Device? = devices.values
            .find { device -> leases.all { it.value.device.deviceId != device.deviceId } }

        return if (device != null) leaseDao.create(device) else null
    }
}