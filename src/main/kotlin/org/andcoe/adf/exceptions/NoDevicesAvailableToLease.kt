package org.andcoe.adf.exceptions

data class NoDevicesAvailableToLease(override val message: String) : RuntimeException()