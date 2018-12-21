package org.andcoe.adf.leases

import org.andcoe.adf.devices.DeviceId
import org.andcoe.adf.exceptions.NoDevicesAvailableToLease

class LeasesResource(private val leaseService: LeaseService) {

    fun create(): Lease {
        val lease = leaseService.create()
        if (lease != null) return lease
        throw NoDevicesAvailableToLease("No devices available to lease.")
    }

    fun create(deviceId: String): Lease {
        val lease = leaseService.create(DeviceId(deviceId))
        if (lease != null) return lease
        throw NoDevicesAvailableToLease("No devices available to lease.")
    }

    fun leases(): List<Lease> {
        return leaseService.leases().map { it.value }
    }
}