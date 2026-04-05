package com.tomtruyen.automation.di

import androidx.room.Room
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.LogcatAutomationLogger
import com.tomtruyen.automation.data.AutomationDatabase
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.data.repository.AutomationRuleRepositoryImpl
import com.tomtruyen.automation.features.actions.ActionExecutor
import com.tomtruyen.automation.features.constraints.ConstraintEvaluator
import com.tomtruyen.automation.features.triggers.TriggerMatcher
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiver
import com.tomtruyen.automation.generated.GeneratedActionProvider
import com.tomtruyen.automation.generated.GeneratedAutomationRegistryProvider
import com.tomtruyen.automation.generated.GeneratedConstraintProvider
import com.tomtruyen.automation.generated.GeneratedReceiverProvider
import com.tomtruyen.automation.generated.GeneratedTriggerProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val automationModule = module {
    // Database
    single {
        Room.databaseBuilder(
            context = get(),
            klass = AutomationDatabase::class.java,
            name = "automation.db"
        ).build()
    }

    // Dao
    single { get<AutomationDatabase>().automationRuleDao() }

    // Repository
    single<AutomationRuleRepository> { AutomationRuleRepositoryImpl(get()) }

    // Definitions
    single {
        GeneratedAutomationRegistryProvider.create()
    }

    // Logger
    single<AutomationLogger> { LogcatAutomationLogger() }

    single<List<TriggerReceiver.TriggerFactory>> {
        GeneratedReceiverProvider.factories
    }

    // Runtime Service
    single { TriggerMatcher(GeneratedTriggerProvider.delegates()) }
    single { ConstraintEvaluator(GeneratedConstraintProvider.delegates()) }
    single { ActionExecutor(androidContext(), GeneratedActionProvider.delegates(androidContext())) }

    single<AutomationRuntimeService> {
        AutomationRuntimeService(
            repository = get(),
            triggerMatcher = get(),
            constraintEvaluator = get(),
            actionExecutor = get(),
        )
    }

}
