package org.andcoe.adf.leases

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.DeviceUtils
import util.DeviceUtils.Companion.DEVICE_PIXEL
import java.util.*

class LeaseDaoTest {

    @Test
    fun createsLease() {
        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)

        val actual = leaseDao.create(DEVICE_PIXEL)
        val expected = Lease(
            leaseId = LeaseId(UUID.fromString(actual.leaseId.id).toString()),
            device = DEVICE_PIXEL
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun returnsEmptyLeases() {
        val leasesDb = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeaseDao(leasesDb)
        assertThat(leaseDao.leases()).isEqualTo(emptyMap<LeaseId, Lease>())
    }

    @Test
    fun returnsLeases() {
        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesDb = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DeviceUtils.DEVICE_SAMSUNG)
        )

        val leaseDao = LeaseDao(leasesDb)
        assertThat(leaseDao.leases()).isEqualTo(
            mapOf(
                leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
                leaseId2 to Lease(leaseId = leaseId2, device = DeviceUtils.DEVICE_SAMSUNG)
            )
        )
    }
}