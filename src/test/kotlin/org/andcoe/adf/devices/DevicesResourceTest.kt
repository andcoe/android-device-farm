package org.andcoe.adf.devices

import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_PIXEL
import org.andcoe.adf.core.AdbOutput.Companion.DEVICE_SAMSUNG
import org.andcoe.adf.exceptions.ResourceNotFound
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class DevicesResourceTest {

    @Test
    fun returnsEmptyDevices() {
        val devicesStore = mutableMapOf<DeviceId, Device>()
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)
        assertThat(deviceResource.devices()).isEqualTo(emptyList<Device>())
    }

    @Test
    fun returnsDevices() {
        val devicesStore = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)
        assertThat(deviceResource.devices()).isEqualTo(
            listOf(
                DEVICE_PIXEL,
                DEVICE_SAMSUNG
            )
        )
    }

    @Test
    fun returnsDeviceById() {
        val devicesStore = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)
        assertThat(deviceResource.devices(DEVICE_PIXEL.deviceId.id)).isEqualTo(DEVICE_PIXEL)
    }

    @Test
    fun throwsIfNoDeviceFoundById() {
        val devicesStore = mutableMapOf(
            DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
            DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
        )
        val deviceDao = DevicesDao(devicesStore)
        val deviceService = DevicesService(deviceDao)
        val deviceResource = DevicesResource(deviceService)
        assertThatThrownBy { deviceResource.devices("random-id") }.isInstanceOf(ResourceNotFound::class.java)
    }
}