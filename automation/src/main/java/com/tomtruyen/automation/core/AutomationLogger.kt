package com.tomtruyen.automation.core

import android.util.Log

interface AutomationLogger {
    fun log(message: String)
}

// Placeholder implementation until persistent automation logging is introduced.
class LogcatAutomationLogger : AutomationLogger {
    override fun log(message: String) {
        Log.d(TAG, message)
    }

    companion object {
        private const val TAG = "AutomationLogger"
    }
}
