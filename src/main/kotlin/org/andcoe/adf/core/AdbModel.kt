package org.andcoe.adf.core

data class AdbDevice(
    val deviceId: String,
    val model: String,
    val manufacturer: String,
    val port: String
)