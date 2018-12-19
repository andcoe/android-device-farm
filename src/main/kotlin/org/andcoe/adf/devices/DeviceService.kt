package org.andcoe.adf.devices

class DeviceService(private val deviceDao: DeviceDao) {

    fun devices(): List<Device> {
        return deviceDao.devices()
    }

    fun createDevice(deviceId: String): Device {
        return deviceDao.create(deviceId)
    }
}