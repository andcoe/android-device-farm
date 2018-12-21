package org.andcoe.adf.leases

import org.andcoe.adf.devices.Device
import java.util.*

class LeasesDao(private val leasesDb: MutableMap<LeaseId, Lease> = mutableMapOf()) {

    fun create(device: Device): Lease {
        val leaseId = LeaseId(UUID.randomUUID().toString())
        val lease = Lease(leaseId = leaseId, device = device)
        leasesDb[leaseId] = lease
        return lease
    }

    fun leases(): Map<LeaseId, Lease> = leasesDb
}