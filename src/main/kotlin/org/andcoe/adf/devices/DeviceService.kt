package org.andcoe.adf.devices

class DeviceService(private val deviceDao: DeviceDao) {

    fun devices(): Map<DeviceId, Device> {
        return deviceDao.devices()
    }

    fun devices(deviceId: DeviceId): Device? {
        return deviceDao.devices(deviceId)
    }

    fun create(deviceId: DeviceId): Device {
        return deviceDao.create(deviceId)
    }

    fun remove(deviceId: DeviceId) {
        deviceDao.remove(deviceId)
    }
}