package util

import org.andcoe.adf.devices.*

class DeviceResourceUtils {

    companion object {
        fun resourceReturningEmptyDevices() = DeviceResource(DeviceService(DeviceDao(mutableMapOf())))
        fun resourceReturningDevices() = DeviceResource(DeviceService(DeviceDao(devices())))

        private fun devices(): MutableMap<DeviceId, Device> {
            return mutableMapOf(
                DeviceId("PIXEL") to Device(DeviceId("PIXEL")),
                DeviceId("SAMSUNG") to Device(DeviceId("SAMSUNG"))
            )
        }
    }
}