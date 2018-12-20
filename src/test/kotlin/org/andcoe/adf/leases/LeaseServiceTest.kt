package org.andcoe.adf.leases

import org.andcoe.adf.devices.Device
import org.andcoe.adf.devices.DeviceDao
import org.andcoe.adf.devices.DeviceId
import org.andcoe.adf.devices.DeviceService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.DeviceUtils
import util.DeviceUtils.Companion.DEVICE_PIXEL
import java.util.*

class LeaseServiceTest {

    @Test
    fun createsLease() {
        val devicesDb = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)

        val actual = leaseService.create()
        val expected = Lease(
            leaseId = LeaseId(actual!!.leaseId.id),
            device = DEVICE_PIXEL
        )

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun returnsNoValueWhenNoDevicesAvailable() {
        val devicesDb = mutableMapOf<DeviceId, Device>()
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)

        val lease = leaseService.create()
        assertThat(lease).isNull()
    }

    @Test
    fun returnsNoValueWhenAllDevicesAreAlreadyLeased() {
        val devicesDb = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesDb = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DeviceUtils.DEVICE_SAMSUNG)
        )
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)

        val lease = leaseService.create()
        assertThat(lease).isNull()
    }
}