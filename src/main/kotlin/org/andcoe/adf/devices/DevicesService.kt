package org.andcoe.adf.devices

import org.andcoe.adf.core.AdbDevice

class DevicesService(private val devicesDao: DevicesDao) {

    fun devices(): Map<DeviceId, Device> = devicesDao.devices()

    fun devices(deviceId: DeviceId): Device? = devicesDao.devices(deviceId)

    fun create(adbDevice: AdbDevice): Device = devicesDao.create(adbDevice)

    fun delete(deviceId: DeviceId) {
        devicesDao.delete(deviceId)
    }
}