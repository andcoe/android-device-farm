package org.andcoe.adf.devices

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.DeviceUtils.Companion.ADB_PIXEL
import util.DeviceUtils.Companion.ADB_SAMSUNG
import util.DeviceUtils.Companion.DEVICE_PIXEL
import util.DeviceUtils.Companion.DEVICE_SAMSUNG

class DevicesServiceTest {

    @Test
    fun returnsEmptyWhenNoDevices() {
        val deviceService = DevicesService(DevicesDao())
        assertThat(deviceService.devices()).isEqualTo(emptyMap<DeviceId, Device>())
    }

    @Test
    fun returnsDevices() {
        val deviceDao = DevicesDao()
        deviceDao.create(ADB_PIXEL)
        deviceDao.create(ADB_SAMSUNG)
        val deviceService = DevicesService(deviceDao)
        assertThat(deviceService.devices()).isEqualTo(
            mapOf(
                DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
                DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
            )
        )
    }

    @Test
    fun returnsDevicesById() {
        val deviceDao = DevicesDao()
        deviceDao.create(ADB_PIXEL)
        deviceDao.create(ADB_SAMSUNG)
        val deviceService = DevicesService(deviceDao)
        assertThat(deviceService.devices(DEVICE_PIXEL.deviceId)).isEqualTo(DEVICE_PIXEL)
    }

    @Test
    fun createsDevice() {
        val deviceService = DevicesService(DevicesDao())
        val newDevice = deviceService.create(ADB_PIXEL)
        assertThat(deviceService.devices()).isEqualTo(
            mapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        )
        assertThat(newDevice).isEqualTo(DEVICE_PIXEL)
    }

    @Test
    fun removesDevice() {
        val deviceDao = DevicesDao()
        deviceDao.create(ADB_PIXEL)
        deviceDao.create(ADB_SAMSUNG)
        val deviceService = DevicesService(deviceDao)
        deviceService.delete(DEVICE_PIXEL.deviceId)
        assertThat(deviceService.devices()).isEqualTo(
            mapOf(DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG)
        )
    }

}
