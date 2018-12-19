package org.andcoe.adf.devices

class DeviceDao {

    private val devicesDb = mutableListOf<Device>()

    fun devices(): List<Device> {
        return devicesDb
    }

    fun create(deviceId: String): Device {
        val device = Device(deviceId)
        devicesDb.add(device)
        return device
    }
}