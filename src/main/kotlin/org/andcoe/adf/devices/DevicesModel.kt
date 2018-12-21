package org.andcoe.adf.devices

data class Device(
    val deviceId: DeviceId,
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val apiLevel: String,
    val port: String
)

data class DeviceId(val id: String)