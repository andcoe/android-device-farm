package org.andcoe.adf.devices

class DeviceService(private val deviceDao: DeviceDao) {

    fun devices(): Map<DeviceId, Device> {
        return deviceDao.devices()
    }

    fun createDevice(deviceId: DeviceId): Device {
        return deviceDao.create(deviceId)
    }
}