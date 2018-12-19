package org.andcoe.adf.devices

class DeviceDao {

    private val devicesDb = mutableMapOf<DeviceId, Device>()

    fun devices(): Map<DeviceId, Device> {
        return devicesDb
    }

    fun create(deviceId: DeviceId): Device {
        val device = Device(deviceId)
        devicesDb[deviceId] = device
        return device
    }

    fun remove(deviceId: DeviceId) {
        devicesDb.remove(deviceId)
    }
}