package org.andcoe.adf.devices

import org.andcoe.adf.exceptions.ResourceNotFound
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import util.DeviceUtils
import util.DeviceUtils.Companion.DEVICE_PIXEL
import util.DeviceUtils.Companion.DEVICE_SAMSUNG

class DeviceResourceTest {

    @Test
    fun returnsEmptyDevices() {
        val devicesDb = mutableMapOf<DeviceId, Device>()
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)
        assertThat(deviceResource.devices()).isEqualTo(emptyList<Device>())
    }

    @Test
    fun returnsDevices() {
        val devicesDb = mutableMapOf(
            DeviceUtils.DEVICE_PIXEL.deviceId to DeviceUtils.DEVICE_PIXEL,
            DeviceUtils.DEVICE_SAMSUNG.deviceId to DeviceUtils.DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)
        assertThat(deviceResource.devices()).isEqualTo(
            listOf(
                DEVICE_PIXEL,
                DEVICE_SAMSUNG
            )
        )
    }

    @Test
    fun returnsDeviceById() {
        val devicesDb = mutableMapOf(
            DeviceUtils.DEVICE_PIXEL.deviceId to DeviceUtils.DEVICE_PIXEL,
            DeviceUtils.DEVICE_SAMSUNG.deviceId to DeviceUtils.DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)
        assertThat(deviceResource.devices(DEVICE_PIXEL.deviceId.id)).isEqualTo(DEVICE_PIXEL)
    }

    @Test
    fun throwsIfNoDeviceFoundById() {
        val devicesDb = mutableMapOf(
            DeviceUtils.DEVICE_PIXEL.deviceId to DeviceUtils.DEVICE_PIXEL,
            DeviceUtils.DEVICE_SAMSUNG.deviceId to DeviceUtils.DEVICE_SAMSUNG
        )
        val deviceDao = DeviceDao(devicesDb)
        val deviceService = DeviceService(deviceDao)
        val deviceResource = DeviceResource(deviceService)
        assertThatThrownBy { deviceResource.devices("random-id") }.isInstanceOf(ResourceNotFound::class.java)
    }
}