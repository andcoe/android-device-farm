package org.andcoe.adf.exceptions

data class ResourceNotFound(override val message: String) : RuntimeException()