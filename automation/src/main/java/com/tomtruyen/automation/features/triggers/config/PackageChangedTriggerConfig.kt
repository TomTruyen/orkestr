package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.PackageChangeType
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(PackageChangedTriggerConfig.DISCRIMINATOR)
data class PackageChangedTriggerConfig(
    val packageName: String = "",
    val changeType: PackageChangeType = PackageChangeType.INSTALLED,
) : TriggerConfig {
    override val type: TriggerType = TriggerType.PACKAGE_CHANGED
    override val category: AutomationCategory = AutomationCategory.UTILITY
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.PACKAGE_CHANGED)

    companion object {
        const val DISCRIMINATOR = "package_changed"
    }
}
