package org.andcoe.adf.devices

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DeviceServiceTest {

    @Test
    fun returnsEmptyWhenNoDevices() {
        val deviceService = DeviceService(DeviceDao())
        assertThat(deviceService.devices()).isEqualTo(emptyMap<DeviceId, Device>())
    }

    @Test
    fun returnsDevices() {
        val deviceDao = DeviceDao()
        deviceDao.create(DeviceId("123"))
        deviceDao.create(DeviceId("456"))
        val deviceService = DeviceService(deviceDao)
        assertThat(deviceService.devices()).isEqualTo(
            mapOf(
                DeviceId("123") to Device(DeviceId("123")),
                DeviceId("456") to Device(DeviceId("456"))
            )
        )
    }

    @Test
    fun createsDevice() {
        val deviceService = DeviceService(DeviceDao())
        val newDevice = deviceService.create(DeviceId("123"))
        assertThat(deviceService.devices()).isEqualTo(
            mapOf(DeviceId("123") to Device(DeviceId("123")))
        )
        assertThat(newDevice).isEqualTo(Device(DeviceId("123")))
    }

    @Test fun removesDevice() {
        val deviceDao = DeviceDao()
        deviceDao.create(DeviceId("123"))
        deviceDao.create(DeviceId("456"))
        val deviceService = DeviceService(deviceDao)
        deviceService.remove(DeviceId("123"))
        assertThat(deviceService.devices()).isEqualTo(
            mapOf(DeviceId("456") to Device(DeviceId("456")))
        )
    }

}
