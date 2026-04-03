package com.tomtruyen.automation.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutomationBroadcastReceiver: BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        scope.launch {
            // TODO: Trigger AutomationRunTimeservice (setup DI first)
        }
    }
}