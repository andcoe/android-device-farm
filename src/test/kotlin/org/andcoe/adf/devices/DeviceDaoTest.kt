package org.andcoe.adf.devices

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DeviceDaoTest {

    @Test
    fun returnsEmptyWhenNoDevices() {
        val deviceDao = DeviceDao()
        assertThat(deviceDao.devices()).isEqualTo(emptyList<Device>())
    }

    @Test
    fun createsDevice() {
        val deviceDao = DeviceDao()
        val newDevice = deviceDao.create("123")
        assertThat(deviceDao.devices()).isEqualTo(listOf(Device("123")))
        assertThat(newDevice).isEqualTo(Device("123"))
    }
}
