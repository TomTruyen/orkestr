package com.tomtruyen.automation.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.features.triggers.receiver.BatteryChangedReceiver
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiver
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class AutomationForegroundService : Service(), KoinComponent {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val service by inject<AutomationRuntimeService>()
    private val repository by inject<AutomationRuleRepository>()

    private val logger by inject<AutomationLogger>()

    private val receivers by inject<List<TriggerReceiver.TriggerFactory>>()

    private val registeredReceivers = mutableMapOf<TriggerReceiver.TriggerFactory, TriggerReceiver>()

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

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

    private fun buildNotification(): Notification {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val contentIntent = launchIntent?.let {
            PendingIntent.getActivity(
                this,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("Automation service running")
            .setContentText("Listening for time, battery, and geofence events")
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(contentIntent)
            .build()
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Automation runtime",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Keeps Orkestr listening for automation triggers."
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun unregisterReceiverSafely(receiver: BroadcastReceiver) {
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
                    logger = logger
                )
            }
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "automation_runtime"
        private const val NOTIFICATION_ID = 1001
        private const val ACTION_START_SERVICE = "com.tomtruyen.automation.action.START_FOREGROUND_SERVICE"
        private const val ACTION_STOP_SERVICE = "com.tomtruyen.automation.action.STOP_FOREGROUND_SERVICE"

        fun start(context: Context) {
            val intent = Intent(context, AutomationForegroundService::class.java).apply {
                action = ACTION_START_SERVICE
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
