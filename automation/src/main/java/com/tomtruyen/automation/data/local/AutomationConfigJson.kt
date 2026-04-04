package com.tomtruyen.automation.data.local

import kotlinx.serialization.json.Json

internal object AutomationConfigJson {
    val instance = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        classDiscriminator = "configType"
    }
}
