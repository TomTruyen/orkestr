package com.tomtruyen.automation.features.triggers.receiver

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.ApplicationLifecycleEvent
import com.tomtruyen.automation.core.model.AppLifecycleTransitionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ApplicationLifecycleReceiver(
    private val appContext: Context,
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
    private val pollIntervalMillis: Long = 5_000,
    private val currentForegroundPackageProvider: (Context) -> String? = ::resolveForegroundPackage,
) : TriggerReceiver() {
    private var lastForegroundPackage: String? = null
    private var monitorJob: Job? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in ACTIONS) return
        scope.launch { pollForegroundPackage() }
    }

    override fun onUnregister(context: Context) {
        monitorJob?.cancel()
        monitorJob = null
    }

    internal fun startMonitoring() {
        if (monitorJob != null) return
        monitorJob = scope.launch {
            lastForegroundPackage = runCatching {
                currentForegroundPackageProvider(appContext)
            }.getOrNull()
            while (isActive) {
                delay(pollIntervalMillis)
                pollForegroundPackage()
            }
        }
    }

    internal suspend fun pollForegroundPackage() {
        val currentPackage = runCatching {
            currentForegroundPackageProvider(appContext)
        }.getOrElse { error ->
            if (error is SecurityException) return
            throw error
        }
        if (currentPackage == lastForegroundPackage) return

        val previousPackage = lastForegroundPackage
        lastForegroundPackage = currentPackage
        logger.log("Foreground application changed from $previousPackage to $currentPackage")

        previousPackage?.let { packageName ->
            service.handleEvent(
                ApplicationLifecycleEvent(
                    packageName = packageName,
                    transitionType = AppLifecycleTransitionType.CLOSED,
                ),
            )
        }

        currentPackage?.let { packageName ->
            service.handleEvent(
                ApplicationLifecycleEvent(
                    packageName = packageName,
                    transitionType = AppLifecycleTransitionType.LAUNCHED,
                ),
            )
        }
    }

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.APPLICATION_LIFECYCLE

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver {
            val receiver = ApplicationLifecycleReceiver(context.applicationContext, service, scope, logger)
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter().apply {
                    ACTIONS.forEach(::addAction)
                },
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            receiver.startMonitoring()
            return receiver
        }

        private val ACTIONS = setOf(
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_CLOSE_SYSTEM_DIALOGS,
        )

        private fun resolveForegroundPackage(context: Context): String? {
            val usageStatsManager = context.getSystemService(UsageStatsManager::class.java) ?: return null
            val now = System.currentTimeMillis()
            return runCatching {
                usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    now - FOREGROUND_APP_LOOKBACK_MILLIS,
                    now,
                ).maxByOrNull { it.lastTimeUsed }?.packageName
            }.getOrNull()
        }

        private const val FOREGROUND_APP_LOOKBACK_MILLIS = 60_000L
    }
}
