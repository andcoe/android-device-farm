package org.andcoe.adf.devices

import org.andcoe.adf.exceptions.ResourceNotFound

class DevicesResource(private val devicesService: DevicesService) {

    fun devices(): List<Device> = devicesService.devices().map { it.value }

    fun devices(deviceId: String): Device {
        val device: Device? = devicesService.devices(DeviceId(deviceId))

        if (device != null) return device

        throw ResourceNotFound("Device with id: '$deviceId' not found.")
    }

}