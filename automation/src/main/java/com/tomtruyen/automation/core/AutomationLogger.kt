package com.tomtruyen.automation.core

import android.util.Log

interface AutomationLogger {
    fun log(message: String)
}

// TODO: Replace with or create a new logger that logs to Room Database
class LogcatAutomationLogger: AutomationLogger {
    override fun log(message: String) {
        Log.d(TAG, message)
    }

    companion object {
        private const val TAG = "AutomationLogger"
    }
}