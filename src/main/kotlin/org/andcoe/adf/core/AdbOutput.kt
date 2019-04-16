package org.andcoe.adf.core

import org.andcoe.adf.devices.Device
import org.andcoe.adf.devices.DeviceId

enum class AdbOutput(val output: String) {
    ADB_KILL_SERVER(""),
    ADB_START_SERVER(
        """
* daemon not running; starting now at tcp:5037
* daemon started successfully
"""
    ),
    ADB_WAIT_FOR_DEVICE(""),
    ADB_DEVICES(
        """
List of devices attached
127.0.0.1:7778	device
127.0.0.1:7777	device
PIXEL	device
SAMSUNG	device


"""
    ),
    ADB_DEVICES_EMPTY(
        """
List of devices attached


"""
    ),
    ADB_TCP_IP(""),
    ADB_FORWARD_IP(""),
    ADB_CONNECT_SUCCESS(
        """
            connected to 127.0.0.1:7777

"""
    ),
    ADB_DEVICE_MODEL_PIXEL(
        """
    Pixel
"""
    ),
    ADB_DEVICE_MANUFACTURER_GOOGLE(
        """
    Google
"""
    ),
    ADB_ANDROID_VERSION_PIXEL(
        """
    9.0
"""
    ),
    ADB_API_LEVEL_PIXEL(
        """
    28
"""
    ),
    ADB_DEVICE_MODEL_S9(
        """
    S9
"""
    ),
    ADB_ANDROID_VERSION_S9(
        """
    8.1
"""
    ),
    ADB_API_LEVEL_S9(
        """
    27
"""
    ),
    ADB_DEVICE_MANUFACTURER_SAMSUNG(
        """
    Samsung
"""
    );

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
    }

}