package org.andcoe.adf.devices

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.DeviceUtils.Companion.ADB_PIXEL
import util.DeviceUtils.Companion.ADB_SAMSUNG
import util.DeviceUtils.Companion.DEVICE_PIXEL
import util.DeviceUtils.Companion.DEVICE_SAMSUNG

class DeviceServiceTest {

    @Test
    fun returnsEmptyWhenNoDevices() {
        val deviceService = DeviceService(DeviceDao())
        assertThat(deviceService.devices()).isEqualTo(emptyMap<DeviceId, Device>())
    }

    @Test
    fun returnsDevices() {
        val deviceDao = DeviceDao()
        deviceDao.create(ADB_PIXEL)
        deviceDao.create(ADB_SAMSUNG)
        val deviceService = DeviceService(deviceDao)
        assertThat(deviceService.devices()).isEqualTo(
            mapOf(
                DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
                DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
            )
        )
    }

    @Test
    fun returnsDevicesById() {
        val deviceDao = DeviceDao()
        deviceDao.create(ADB_PIXEL)
        deviceDao.create(ADB_SAMSUNG)
        val deviceService = DeviceService(deviceDao)
        assertThat(deviceService.devices(DEVICE_PIXEL.deviceId)).isEqualTo(DEVICE_PIXEL)
    }

    @Test
    fun createsDevice() {
        val deviceService = DeviceService(DeviceDao())
        val newDevice = deviceService.create(ADB_PIXEL)
        assertThat(deviceService.devices()).isEqualTo(
            mapOf(DEVICE_PIXEL.deviceId to DEVICE_PIXEL)
        )
        assertThat(newDevice).isEqualTo(DEVICE_PIXEL)
    }

    @Test
    fun removesDevice() {
        val deviceDao = DeviceDao()
        deviceDao.create(ADB_PIXEL)
        deviceDao.create(ADB_SAMSUNG)
        val deviceService = DeviceService(deviceDao)
        deviceService.remove(DEVICE_PIXEL.deviceId)
        assertThat(deviceService.devices()).isEqualTo(
            mapOf(DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG)
        )
    }

}
