package org.andcoe.adf.leases

import org.andcoe.adf.devices.DeviceService

class LeaseService(private val deviceService: DeviceService,
                   private val leaseDao: LeaseDao) {

    fun create(): Lease? {
        val devices = deviceService.devices()
        val device = devices.values.firstOrNull() ?: return null
        return leaseDao.create(device)
    }
}