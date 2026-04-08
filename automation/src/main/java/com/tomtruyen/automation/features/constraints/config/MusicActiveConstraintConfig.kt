package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(MusicActiveConstraintConfig.DISCRIMINATOR)
data class MusicActiveConstraintConfig(val active: Boolean = true) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.MUSIC_ACTIVE
    override val category: AutomationCategory = AutomationCategory.VOLUME

    companion object {
        const val DISCRIMINATOR = "music_active_constraint"
    }
}
