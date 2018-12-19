package org.andcoe.adf.devices

class DeviceResource(private val deviceService: DeviceService) {

    fun devices(): List<Device> = deviceService.devices().map { it.value }

}