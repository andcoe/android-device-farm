package org.andcoe.adf.devices

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.DeviceResourceUtils.Companion.resourceReturningDevices
import util.DeviceResourceUtils.Companion.resourceReturningEmptyDevices

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
                Device(DeviceId("PIXEL")),
                Device(DeviceId("SAMSUNG"))
            )
        )
    }

    @Test
    fun returnsDeviceById() {
        val deviceResource = resourceReturningDevices()
        assertThat(deviceResource.devices("PIXEL")).isEqualTo(Device(DeviceId("PIXEL")))
    }
}