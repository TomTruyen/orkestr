package com.tomtruyen.automation.di

import androidx.room.Room
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.LogcatAutomationLogger
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.data.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.data.definition.BatteryLevelConstraintDefinition
import com.tomtruyen.automation.data.definition.ChargeStateTriggerDefinition
import com.tomtruyen.automation.data.definition.LogMessageActionDefinition
import com.tomtruyen.automation.data.definition.ShowNotificationActionDefinition
import com.tomtruyen.automation.data.AutomationDatabase
import com.tomtruyen.automation.data.repository.RoomAutomationRuleRepository
import com.tomtruyen.automation.features.actions.delegate.ActionDelegate
import com.tomtruyen.automation.features.actions.ActionExecutor
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.delegate.ConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintEvaluator
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.delegate.TriggerDelegate
import com.tomtruyen.automation.features.triggers.TriggerMatcher
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.automation.features.triggers.delegate.ChargeStateTriggerDelegate
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
    single<AutomationRuleRepository> { RoomAutomationRuleRepository(get()) }

    // Definitions
    single {
        AutomationDefinitionRegistry(
            triggers = listOf(ChargeStateTriggerDefinition),
            constraints = listOf(BatteryLevelConstraintDefinition),
            actions = listOf(ShowNotificationActionDefinition, LogMessageActionDefinition)
        )
    }

    // Logger
    single<AutomationLogger> { LogcatAutomationLogger() }

    // Runtime Service
    single { TriggerMatcher() }
    single { ConstraintEvaluator() }
    single { ActionExecutor() }

    single<AutomationRuntimeService> {
        AutomationRuntimeService(
            repository = get(),
            triggerMatcher = get(),
            constraintEvaluator = get(),
            actionExecutor = get(),
        )
    }

}
