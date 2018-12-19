package org.andcoe.adf.devices

class DeviceDao(private val devicesDb: MutableMap<DeviceId, Device> = mutableMapOf()) {


    fun devices(): Map<DeviceId, Device> = devicesDb

    fun devices(deviceId: DeviceId): Device? = devicesDb[deviceId]

    fun create(deviceId: DeviceId): Device {
        val device = Device(deviceId)
        devicesDb[deviceId] = device
        return device
    }

    fun remove(deviceId: DeviceId) {
        devicesDb.remove(deviceId)
    }
}