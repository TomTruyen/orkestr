package com.tomtruyen.automation.core

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.core.notification.AutomationNotificationService
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiver
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class AutomationForegroundService :
    Service(),
    KoinComponent {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val service by inject<AutomationRuntimeService>()
    private val repository by inject<AutomationRuleRepository>()

    private val logger by inject<AutomationLogger>()

    private val receivers by inject<List<TriggerReceiver.TriggerFactory>>()
    private val notificationService by lazy { AutomationNotificationService(this) }

    private val registeredReceivers = mutableMapOf<TriggerReceiver.TriggerFactory, TriggerReceiver>()

    override fun onCreate() {
        super.onCreate()

        notificationService.ensureRuntimeChannel()
        startForeground(NOTIFICATION_ID, notificationService.buildRuntimeNotification())

        scope.launch {
            repository.observeRules()
                .map { rules ->
                    rules
                        .asSequence()
                        .filter { it.enabled }
                        .flatMap { rule ->
                            rule.triggers.asSequence().flatMap { trigger ->
                                trigger.requiredReceiverKeys.asSequence()
                            }
                        }
                        .toSet()
                }
                .distinctUntilChanged()
                .collect { activeReceiverKeys ->
                    syncReceivers(activeReceiverKeys)
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopSelf()
            return START_NOT_STICKY
        }

        return START_STICKY
    }

    override fun onDestroy() {
        registeredReceivers.values.forEach { receiver ->
            unregisterReceiverSafely(receiver)
        }
        registeredReceivers.clear()

        scope.cancel()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun unregisterReceiverSafely(receiver: TriggerReceiver) {
        receiver.onUnregister(this)
        runCatching { unregisterReceiver(receiver) }
    }

    private fun syncReceivers(activeReceiverKeys: Set<TriggerReceiverKey>) {
        val requiredFactories = receivers.filter { factory ->
            factory.key in activeReceiverKeys
        }.toSet()

        registeredReceivers.keys
            .filterNot(requiredFactories::contains)
            .forEach { factory ->
                registeredReceivers.remove(factory)?.let(::unregisterReceiverSafely)
            }

        requiredFactories
            .filterNot(registeredReceivers::containsKey)
            .forEach { factory ->
                registeredReceivers[factory] = factory.register(
                    context = this,
                    service = service,
                    scope = scope,
                    logger = logger,
                )
            }
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val ACTION_START_SERVICE = "com.tomtruyen.automation.action.START_FOREGROUND_SERVICE"
        private const val ACTION_STOP_SERVICE = "com.tomtruyen.automation.action.STOP_FOREGROUND_SERVICE"

        fun start(context: Context) {
            val intent = Intent(context, AutomationForegroundService::class.java).apply {
                action = ACTION_START_SERVICE
            }
            runCatching {
                ContextCompat.startForegroundService(context, intent)
            }.getOrElse { error ->
                val isStartNotAllowed =
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        error is ForegroundServiceStartNotAllowedException
                if (!isStartNotAllowed) {
                    throw error
                }
            }
        }
    }
}
