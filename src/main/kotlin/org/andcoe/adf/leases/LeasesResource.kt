package org.andcoe.adf.leases

import org.andcoe.adf.devices.DeviceId
import org.andcoe.adf.exceptions.NoDevicesAvailableToLease

class LeasesResource(private val leasesService: LeasesService) {

    fun create(): Lease {
        val lease = leasesService.create()
        if (lease != null) return lease
        throw NoDevicesAvailableToLease("No devices available to lease.")
    }

    fun create(deviceId: String): Lease {
        val lease = leasesService.create(DeviceId(deviceId))
        if (lease != null) return lease
        throw NoDevicesAvailableToLease("No devices available to lease.")
    }

    fun leases(): List<Lease> {
        return leasesService.leases().map { it.value }
    }

    fun delete(leaseId: String) {
        leasesService.delete(leaseId)
    }
}