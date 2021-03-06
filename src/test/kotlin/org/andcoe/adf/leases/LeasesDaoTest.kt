package org.andcoe.adf.leases

import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_PIXEL
import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_SAMSUNG
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class LeasesDaoTest {

    @Test
    fun createsLease() {
        val leasesStore = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesStore)

        val actual = leaseDao.create(DEVICE_PIXEL)
        val expected = Lease(
            leaseId = LeaseId(UUID.fromString(actual.leaseId.id).toString()),
            device = DEVICE_PIXEL
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun returnsEmptyLeases() {
        val leasesStore = mutableMapOf<LeaseId, Lease>()
        val leaseDao = LeasesDao(leasesStore)
        assertThat(leaseDao.leases()).isEqualTo(emptyMap<LeaseId, Lease>())
    }

    @Test
    fun returnsLeases() {
        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesStore = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeasesDao(leasesStore)
        assertThat(leaseDao.leases()).isEqualTo(
            mapOf(
                leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
                leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
            )
        )
    }

    @Test
    fun deletesLease() {
        val leaseId1 = LeaseId(UUID.randomUUID().toString())
        val leaseId2 = LeaseId(UUID.randomUUID().toString())

        val leasesStore = mutableMapOf(
            leaseId1 to Lease(leaseId = leaseId1, device = DEVICE_PIXEL),
            leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
        )

        val leaseDao = LeasesDao(leasesStore)

        leaseDao.delete(leaseId = leaseId1.id)

        assertThat(leaseDao.leases()).isEqualTo(
            mapOf(
                leaseId2 to Lease(leaseId = leaseId2, device = DEVICE_SAMSUNG)
            )
        )
    }
}