package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.PackageChangedEvent
import com.tomtruyen.automation.core.model.PackageChangeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PackageChangedReceiver(
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
) : TriggerReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart?.takeIf(String::isNotBlank) ?: return
        val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
        val changeType = when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> if (replacing) PackageChangeType.UPDATED else PackageChangeType.INSTALLED
            Intent.ACTION_PACKAGE_REMOVED -> if (replacing) return else PackageChangeType.REMOVED
            Intent.ACTION_PACKAGE_REPLACED -> PackageChangeType.UPDATED
            else -> return
        }
        logger.info("Received package change event package=$packageName type=${changeType.name}")
        scope.launch {
            service.handleEvent(PackageChangedEvent(packageName, changeType))
        }
    }

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.PACKAGE_CHANGED

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver {
            val receiver = PackageChangedReceiver(service, scope, logger)
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter().apply {
                    addAction(Intent.ACTION_PACKAGE_ADDED)
                    addAction(Intent.ACTION_PACKAGE_REMOVED)
                    addAction(Intent.ACTION_PACKAGE_REPLACED)
                    addDataScheme("package")
                },
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            return receiver
        }
    }
}
