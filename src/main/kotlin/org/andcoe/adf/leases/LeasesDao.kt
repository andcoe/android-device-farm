package org.andcoe.adf.leases

import org.andcoe.adf.devices.Device
import java.util.*

class LeasesDao(private val leasesStore: MutableMap<LeaseId, Lease> = mutableMapOf()) {

    fun create(device: Device): Lease {
        val leaseId = LeaseId(UUID.randomUUID().toString())
        val lease = Lease(leaseId = leaseId, device = device)
        leasesStore[leaseId] = lease
        return lease
    }

    fun leases(): Map<LeaseId, Lease> = leasesStore
}