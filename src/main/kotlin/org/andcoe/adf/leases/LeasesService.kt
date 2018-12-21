package org.andcoe.adf.leases

import org.andcoe.adf.devices.Device
import org.andcoe.adf.devices.DeviceId
import org.andcoe.adf.devices.DevicesService
import org.andcoe.adf.exceptions.DeviceNotFound

class LeasesService(
    private val devicesService: DevicesService,
    private val leasesDao: LeasesDao
) {

    fun create(): Lease? {
        val leases = leasesDao.leases()
        val devices = devicesService.devices()

        val device: Device? = devices.values
            .find { device -> leases.all { it.value.device.deviceId != device.deviceId } }

        return if (device != null) leasesDao.create(device) else null
    }

    fun create(deviceId: DeviceId): Lease? {
        val devices = devicesService.devices()
        val device: Device = devices[deviceId] ?: throw DeviceNotFound("No device found with id: '${deviceId.id}'")

        val leases = leasesDao.leases()
        val leaseNotFound = leases.all { lease -> lease.value.device.deviceId != device.deviceId }
        return if (leaseNotFound) leasesDao.create(device) else null
    }

    fun leases(): Map<LeaseId, Lease> = leasesDao.leases()
}