package org.andcoe.adf.exceptions

data class DeviceNotFound(override val message: String) : RuntimeException()