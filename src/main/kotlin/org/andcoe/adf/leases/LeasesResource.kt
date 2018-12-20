package org.andcoe.adf.leases

import org.andcoe.adf.exceptions.NoDevicesAvailableToLease

class LeasesResource(private val leaseService: LeaseService) {

    fun create(): Lease {
        val lease = leaseService.create()
        if (lease != null) return lease
        throw NoDevicesAvailableToLease("No devices available to lease.")
    }
}