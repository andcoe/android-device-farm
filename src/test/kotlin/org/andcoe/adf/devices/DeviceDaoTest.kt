package org.andcoe.adf.devices

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DeviceDaoTest {

    @Test
    fun returnsEmptyWhenNoDevices() {
        val deviceDao = DeviceDao()
        assertThat(deviceDao.devices()).isEqualTo(emptyMap<DeviceId, Device>())
    }

    @Test
    fun createsDevice() {
        val deviceDao = DeviceDao()
        val newDevice = deviceDao.create(DeviceId("123"))
        assertThat(deviceDao.devices()).isEqualTo(mapOf(DeviceId("123") to Device(DeviceId("123"))))
        assertThat(newDevice).isEqualTo(Device(DeviceId("123")))
    }
}
