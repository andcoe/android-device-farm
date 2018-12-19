package org.andcoe.adf.devices

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.DeviceUtils
import util.DeviceUtils.Companion.ADB_PIXEL
import util.DeviceUtils.Companion.ADB_SAMSUNG
import util.DeviceUtils.Companion.DEVICE_PIXEL
import util.DeviceUtils.Companion.DEVICE_SAMSUNG

class DeviceDaoTest {

    @Test
    fun returnsEmptyWhenNoDevices() {
        val deviceDao = DeviceDao()
        assertThat(deviceDao.devices()).isEqualTo(emptyMap<DeviceId, Device>())
    }

    @Test
    fun createsDevice() {
        val deviceDao = DeviceDao()
        val newDevice = deviceDao.create(DeviceUtils.ADB_PIXEL)
        assertThat(deviceDao.devices()).isEqualTo(mapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL))
        assertThat(newDevice).isEqualTo(DEVICE_PIXEL)
    }

    @Test
    fun removesDevice() {
        val deviceDao = DeviceDao()
        deviceDao.create(ADB_PIXEL)
        deviceDao.create(ADB_SAMSUNG)
        deviceDao.remove(DEVICE_PIXEL.deviceId)
        assertThat(deviceDao.devices()).isEqualTo(mapOf(DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG))
    }
}
