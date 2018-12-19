package org.andcoe.adf.devices

import org.andcoe.adf.exceptions.ResourceNotFound

class DeviceResource(private val deviceService: DeviceService) {

    fun devices(): List<Device> = deviceService.devices().map { it.value }

    fun devices(deviceId: String): Device {
        val device: Device? = deviceService.devices(DeviceId(deviceId))
        if (device != null) {
            return device
        }

        throw ResourceNotFound("Device with id: '$deviceId' not found.")
    }

}