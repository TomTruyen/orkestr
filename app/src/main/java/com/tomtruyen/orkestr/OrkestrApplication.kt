package com.tomtruyen.orkestr

import android.app.Application
import com.tomtruyen.automation.core.AutomationForegroundService
import com.tomtruyen.automation.di.automationModule
import com.tomtruyen.orkestr.features.automation.di.automationFeatureModule
import com.tomtruyen.orkestr.features.geofence.di.geofenceFeatureModule
import com.tomtruyen.orkestr.ui.common.di.commonFeatureModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class OrkestrApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@OrkestrApplication)
            modules(
                automationModule,
                commonFeatureModule,
                geofenceFeatureModule,
                automationFeatureModule,
            )
        }

        AutomationForegroundService.start(this)
    }
}
