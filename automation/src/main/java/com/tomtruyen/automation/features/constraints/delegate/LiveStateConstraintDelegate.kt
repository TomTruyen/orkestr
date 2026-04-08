package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.state.DeviceStateReader
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig

abstract class LiveStateConstraintDelegate<T : ConstraintConfig>(context: Context) : ConstraintDelegate<T> {
    protected val deviceStateReader = DeviceStateReader(context.applicationContext)

    final override suspend fun evaluate(config: T, event: AutomationEvent): Boolean = evaluate(config)

    protected abstract suspend fun evaluate(config: T): Boolean
}
