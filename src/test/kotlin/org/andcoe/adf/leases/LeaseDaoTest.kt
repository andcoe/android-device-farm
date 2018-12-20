package org.andcoe.adf.leases

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
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
}