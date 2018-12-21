package org.andcoe.adf.leases

import org.andcoe.adf.devices.Device
import org.andcoe.adf.devices.DeviceId
import org.andcoe.adf.devices.DeviceService
import org.andcoe.adf.exceptions.DeviceNotFound

class LeaseService(private val deviceService: DeviceService,
                   private val leaseDao: LeaseDao) {

    fun create(): Lease? {
        val leases = leaseDao.leases()
        val devices = deviceService.devices()

        val device: Device? = devices.values
            .find { device -> leases.all { it.value.device.deviceId != device.deviceId } }

        return if (device != null) leaseDao.create(device) else null
    }

    fun create(deviceId: DeviceId): Lease? {
        val devices = deviceService.devices()
        val device: Device = devices[deviceId] ?: throw DeviceNotFound("No device found with id: '${deviceId.id}'")

        val leases = leaseDao.leases()
        val leaseNotFound = leases.all { lease -> lease.value.device.deviceId != device.deviceId }
        return if (leaseNotFound) leaseDao.create(device) else null
    }
}