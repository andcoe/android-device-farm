package org.andcoe.adf.devices

import org.andcoe.adf.exceptions.ResourceNotFound
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import util.DeviceUtils.Companion.DEVICE_PIXEL
import util.DeviceUtils.Companion.DEVICE_SAMSUNG
import util.DeviceUtils.Companion.resourceReturningDevices
import util.DeviceUtils.Companion.resourceReturningEmptyDevices

class DeviceResourceTest {

    @Test
    fun returnsEmptyDevices() {
        val deviceResource = resourceReturningEmptyDevices()
        assertThat(deviceResource.devices()).isEqualTo(emptyList<Device>())
    }

    @Test
    fun returnsDevices() {
        val deviceResource = resourceReturningDevices()
        assertThat(deviceResource.devices()).isEqualTo(
            listOf(
                DEVICE_PIXEL,
                DEVICE_SAMSUNG
            )
        )
    }

    @Test
    fun returnsDeviceById() {
        val deviceResource = resourceReturningDevices()
        assertThat(deviceResource.devices(DEVICE_PIXEL.deviceId.id)).isEqualTo(DEVICE_PIXEL)
    }

    @Test
    fun throwsIfNoDeviceFoundById() {
        val deviceResource = resourceReturningDevices()
        assertThatThrownBy { deviceResource.devices("random-id") }.isInstanceOf(ResourceNotFound::class.java)
    }
}