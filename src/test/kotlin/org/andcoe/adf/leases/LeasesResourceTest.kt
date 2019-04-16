package org.andcoe.adf.leases

import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_PIXEL
import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_SAMSUNG
import org.andcoe.adf.devices.DevicesDao
import org.andcoe.adf.devices.DevicesService
import org.andcoe.adf.exceptions.DeviceNotFound
import org.andcoe.adf.exceptions.NoDevicesAvailableToLease
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.util.*

class LeasesResourceTest {

    @Test
    fun createsLeaseForAnyDevice() {
        val devicesStore = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)

        val leasesStore = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
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
        val deviceDao = DevicesDao(devicesStore = mutableMapOf())
        val deviceService = DevicesService(deviceDao)

        val leasesStore = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        assertThatThrownBy { leaseResource.create() }
            .isInstanceOf(NoDevicesAvailableToLease::class.java)
    }

    @Test
    fun returnsErrorIfAllDevicesAlreadyLeased() {
        val devicesStore = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesStore = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        assertThatThrownBy { leaseResource.create() }
            .isInstanceOf(NoDevicesAvailableToLease::class.java)
    }

    @Test
    fun createsLeaseForSpecificDevice() {
        val devicesStore = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)

        val leasesStore = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
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
        val devicesStore = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesStore = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        assertThatThrownBy { leaseResource.create(DEVICE_PIXEL.deviceId.id) }
            .isInstanceOf(NoDevicesAvailableToLease::class.java)
    }

    @Test
    fun returnsErrorWhenSpecificDeviceIdNotFound() {
        val devicesStore = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesStore = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        assertThatThrownBy { leaseResource.create("some-random-device-id") }
            .isInstanceOf(DeviceNotFound::class.java)
    }

    @Test
    fun returnsAllLeases() {
        val devicesStore = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesStore = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        assertThat(leaseResource.leases())
            .isEqualTo(
                listOf(
                    Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
                    Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
                )
            )
    }

    @Test
    fun deletesLease() {
        val devicesStore = mutableMapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)

        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesStore = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeasesDao(leasesStore)
        val leaseService = LeasesService(deviceService, leaseDao)
        val leaseResource = LeasesResource(leaseService)

        leaseResource.delete(leaseId1.id)

        assertThat(leaseResource.leases())
            .isEqualTo(
                listOf(
                    Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
                )
            )
    }

}