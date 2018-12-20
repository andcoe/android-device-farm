package org.andcoe.adf.leases

import org.andcoe.adf.devices.Device

data class Lease(val leaseId: LeaseId, val device: Device)
data class LeaseId(val id: String)