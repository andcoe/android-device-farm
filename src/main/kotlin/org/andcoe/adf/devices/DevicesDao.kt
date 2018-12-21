package org.andcoe.adf.devices

import org.andcoe.adf.core.AdbDevice

class DevicesDao(private val devicesDb: MutableMap<DeviceId, Device> = mutableMapOf()) {

    fun devices(): Map<DeviceId, Device> = devicesDb

    fun devices(deviceId: DeviceId): Device? = devicesDb[deviceId]

    fun create(adbDevice: AdbDevice): Device {
        val device = Device(
            deviceId = DeviceId(adbDevice.deviceId),
            model = adbDevice.model,
            manufacturer = adbDevice.manufacturer,
            androidVersion = adbDevice.androidVersion,
            apiLevel = adbDevice.apiLevel,
            port = adbDevice.port
        )
        devicesDb[device.deviceId] = device
        return device
    }

    fun remove(deviceId: DeviceId) {
        devicesDb.remove(deviceId)
    }
}