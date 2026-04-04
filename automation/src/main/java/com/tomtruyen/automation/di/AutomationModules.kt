package com.tomtruyen.automation.di

import androidx.room.Room
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.LogcatAutomationLogger
import com.tomtruyen.automation.data.AutomationDatabase
import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.features.constraints.definition.BatteryLevelConstraintDefinition
import com.tomtruyen.automation.features.triggers.definition.ChargeStateTriggerDefinition
import com.tomtruyen.automation.features.actions.definition.DoNotDisturbActionDefinition
import com.tomtruyen.automation.features.actions.definition.LogMessageActionDefinition
import com.tomtruyen.automation.features.actions.definition.ShowNotificationActionDefinition
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.data.repository.AutomationRuleRepositoryImpl
import com.tomtruyen.automation.features.actions.ActionExecutor
import com.tomtruyen.automation.features.constraints.ConstraintEvaluator
import com.tomtruyen.automation.features.triggers.TriggerMatcher
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
        AutomationDefinitionRegistry(
            triggers = listOf(ChargeStateTriggerDefinition),
            constraints = listOf(BatteryLevelConstraintDefinition),
            actions = listOf(
                ShowNotificationActionDefinition,
                LogMessageActionDefinition,
                DoNotDisturbActionDefinition
            )
        )
    }

    // Logger
    single<AutomationLogger> { LogcatAutomationLogger() }

    // Runtime Service
    single { TriggerMatcher() }
    single { ConstraintEvaluator() }
    single { ActionExecutor(androidContext()) }

    single<AutomationRuntimeService> {
        AutomationRuntimeService(
            repository = get(),
            triggerMatcher = get(),
            constraintEvaluator = get(),
            actionExecutor = get(),
        )
    }

}
