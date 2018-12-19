package util

import org.andcoe.adf.core.AdbDevice
import org.andcoe.adf.devices.*

class DeviceUtils {

    companion object {
        val DEVICE_PIXEL = Device(DeviceId("PIXEL"), "Pixel", "Google", "7777")
        val ADB_PIXEL = AdbDevice(
            deviceId = DEVICE_PIXEL.deviceId.id,
            model = DEVICE_PIXEL.model,
            manufacturer = DEVICE_PIXEL.manufacturer,
            port = DEVICE_PIXEL.port
        )

        val DEVICE_SAMSUNG = Device(DeviceId("SAMSUNG"), "S9", "Samsung", "7778")
        val ADB_SAMSUNG = AdbDevice(
            deviceId = DEVICE_SAMSUNG.deviceId.id,
            model = DEVICE_SAMSUNG.model,
            manufacturer = DEVICE_SAMSUNG.manufacturer,
            port = DEVICE_SAMSUNG.port
        )

        fun resourceReturningEmptyDevices() = DeviceResource(DeviceService(DeviceDao(mutableMapOf())))
        fun resourceReturningDevices() = DeviceResource(DeviceService(DeviceDao(devices())))

        private fun devices(): MutableMap<DeviceId, Device> {
            return mutableMapOf(
                DEVICE_PIXEL.deviceId to DEVICE_PIXEL,
                DEVICE_SAMSUNG.deviceId to DEVICE_SAMSUNG
            )
        }
    }
}