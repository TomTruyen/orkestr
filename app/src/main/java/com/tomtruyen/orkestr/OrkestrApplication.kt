package com.tomtruyen.orkestr

import android.app.Application
import com.tomtruyen.automation.core.AutomationForegroundService
import com.tomtruyen.automation.di.automationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class OrkestrApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@OrkestrApplication)
            modules(automationModule)
        }

        AutomationForegroundService.start(this)
    }
}
