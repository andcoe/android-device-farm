package org.andcoe.adf.devices

import org.andcoe.adf.core.AdbOutput.Companion.ADB_PIXEL
import org.andcoe.adf.core.AdbOutput.Companion.ADB_SAMSUNG
import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_PIXEL
import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_SAMSUNG
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DevicesDaoTest {

    @Test
    fun returnsEmptyWhenNoDevices() {
        val deviceDao = DevicesDao()
        assertThat(deviceDao.devices()).isEqualTo(emptyMap<DeviceId, Device>())
    }

    @Test
    fun createsDevice() {
        val deviceDao = DevicesDao()
        val newDevice = deviceDao.create(ADB_PIXEL)
        assertThat(deviceDao.devices()).isEqualTo(mapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL))
        assertThat(newDevice).isEqualTo(DEVICE_PIXEL)
    }

    @Test
    fun removesDevice() {
        val deviceDao = DevicesDao()
        deviceDao.create(ADB_PIXEL)
        deviceDao.create(ADB_SAMSUNG)
        deviceDao.delete(DEVICE_PIXEL.deviceId)
        assertThat(deviceDao.devices()).isEqualTo(mapOf(DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG))
    }
}
