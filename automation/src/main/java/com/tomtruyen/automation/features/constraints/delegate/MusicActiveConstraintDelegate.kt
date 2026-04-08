package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.MusicActiveConstraintConfig

@GenerateConstraintDelegate
class MusicActiveConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<MusicActiveConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.MUSIC_ACTIVE

    override suspend fun evaluate(config: MusicActiveConstraintConfig): Boolean =
        deviceStateReader.isMusicActive() == config.active
}
