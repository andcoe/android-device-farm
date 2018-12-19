package org.andcoe.adf.devices

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DeviceServiceTest {

    @Test fun returnsEmptyWhenNoDevices() {
        val deviceDao = DeviceDao()
        val deviceService = DeviceService(deviceDao)
        assertThat(deviceService.devices()).isEqualTo(emptyList<Device>())
    }

    @Test fun returnsDevices() {
        val deviceDao = DeviceDao()
        deviceDao.create("123")
        deviceDao.create("456")
        val deviceService = DeviceService(deviceDao)
        assertThat(deviceService.devices()).isEqualTo(listOf(Device("123"), Device("456")))
    }

    @Test fun createsDevice() {
        val deviceDao = DeviceDao()
        val deviceService = DeviceService(deviceDao)
        val newDevice = deviceService.createDevice("123")
        assertThat(deviceService.devices()).isEqualTo(listOf(Device("123")))
        assertThat(newDevice).isEqualTo(Device("123"))
    }

}
