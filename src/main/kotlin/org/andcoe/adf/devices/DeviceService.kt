package org.andcoe.adf.devices

import org.andcoe.adf.core.AdbDevice

class DeviceService(private val deviceDao: DeviceDao) {

    fun devices(): Map<DeviceId, Device> {
        return deviceDao.devices()
    }

    fun devices(deviceId: DeviceId): Device? {
        return deviceDao.devices(deviceId)
    }

    fun create(adbDevice: AdbDevice): Device {
        return deviceDao.create(adbDevice)
    }

    fun remove(deviceId: DeviceId) {
        deviceDao.remove(deviceId)
    }
}