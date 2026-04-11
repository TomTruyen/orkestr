package com.tomtruyen.automation.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class PackageChangeType {
    INSTALLED,
    REMOVED,
    UPDATED,
}
