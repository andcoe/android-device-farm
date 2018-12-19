package util

import org.andcoe.adf.core.AdbDevice
import org.andcoe.adf.devices.*

class DeviceUtils {

    companion object {
        val DEVICE_PIXEL = Device(
            deviceId = DeviceId("PIXEL"),
            model = "Pixel",
            manufacturer = "Google",
            androidVersion = "9.0",
            apiLevel = "28",
            port = "7777"
        )
        val ADB_PIXEL = AdbDevice(
            deviceId = DEVICE_PIXEL.deviceId.id,
            model = DEVICE_PIXEL.model,
            manufacturer = DEVICE_PIXEL.manufacturer,
            androidVersion = DEVICE_PIXEL.androidVersion,
            apiLevel = DEVICE_PIXEL.apiLevel,
            port = DEVICE_PIXEL.port
        )
        val DEVICE_SAMSUNG = Device(
            deviceId = DeviceId("SAMSUNG"),
            model = "S9",
            manufacturer = "Samsung",
            androidVersion = "8.1",
            apiLevel = "27",
            port = "7778"
        )
        val ADB_SAMSUNG = AdbDevice(
            deviceId = DEVICE_SAMSUNG.deviceId.id,
            model = DEVICE_SAMSUNG.model,
            manufacturer = DEVICE_SAMSUNG.manufacturer,
            androidVersion = DEVICE_SAMSUNG.androidVersion,
            apiLevel = DEVICE_SAMSUNG.apiLevel,
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