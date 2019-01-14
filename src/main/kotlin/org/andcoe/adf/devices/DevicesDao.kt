package org.andcoe.adf.devices

import org.andcoe.adf.core.AdbDevice

class DevicesDao(private val devicesStore: MutableMap<DeviceId, Device> = mutableMapOf()) {

    fun devices(): Map<DeviceId, Device> = devicesStore

    fun devices(deviceId: DeviceId): Device? = devicesStore[deviceId]

    fun create(adbDevice: AdbDevice): Device {
        val device = Device(
            deviceId = DeviceId(adbDevice.deviceId),
            model = adbDevice.model,
            manufacturer = adbDevice.manufacturer,
            androidVersion = adbDevice.androidVersion,
            apiLevel = adbDevice.apiLevel,
            port = adbDevice.port
        )
        devicesStore[device.deviceId] = device
        return device
    }

    fun delete(deviceId: DeviceId) {
        devicesStore.remove(deviceId)
    }
}