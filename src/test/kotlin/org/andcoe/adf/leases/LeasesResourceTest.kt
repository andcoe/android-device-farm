package org.andcoe.adf.leases

import org.andcoe.adf.devices.DeviceDao
import org.andcoe.adf.devices.DeviceService
import org.andcoe.adf.exceptions.DeviceNotFound
import org.andcoe.adf.exceptions.NoDevicesAvailableToLease
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import util.DeviceUtils.Companion.DEVICE_PIXEL
import util.DeviceUtils.Companion.DEVICE_SAMSUNG
import java.util.*

class LeasesResourceTest {

    @Test
    fun createsLeaseForAnyDevice() {
        val devicesDb = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)


        val actual = leaseResource.create()
        val expected = Lease(
            leaseId = LeaseId(UUID.fromString(actual.leaseId.id).toString()),
            device = DEVICE_PIXEL
        )

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun returnsErrorWhenNoFreeDevicesToLease() {
        val deviceDao = DeviceDao(devicesDb = mutableMapOf())
        val deviceService = DeviceService(deviceDao)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        assertThatThrownBy { leaseResource.create() }
            .isInstanceOf(NoDevicesAvailableToLease::class.java)
    }

    @Test
    fun returnsErrorIfAllDevicesAlreadyLeased() {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesDb = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        assertThatThrownBy { leaseResource.create() }
            .isInstanceOf(NoDevicesAvailableToLease::class.java)
    }

    @Test
    fun createsLeaseForSpecificDevice() {
        val devicesDb = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)

        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)


        val actual = leaseResource.create(DEVICE_PIXEL.deviceId.id)
        val expected = Lease(
            leaseId = LeaseId(UUID.fromString(actual.leaseId.id).toString()),
            device = DEVICE_PIXEL
        )

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun returnsErrorIfDeviceAlreadyLeased() {
        val devicesDb = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesDb = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        assertThatThrownBy { leaseResource.create(DEVICE_PIXEL.deviceId.id) }
            .isInstanceOf(NoDevicesAvailableToLease::class.java)
    }

    @Test
    fun returnsErrorWhenSpecificDeviceIdNotFound() {
        val devicesDb = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesDb = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        assertThatThrownBy { leaseResource.create("some-random-device-id") }
            .isInstanceOf(DeviceNotFound::class.java)
    }

    @Test
    fun returnsAllLeases() {
        val devicesDb = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesDb = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeaseDao(leasesDb)
        val leaseService = LeaseService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        assertThat(leaseResource.leases())
            .isEqualTo(
                listOf(
                    Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
                    Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
                )
            )
    }
}