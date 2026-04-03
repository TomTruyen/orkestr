package com.tomtruyen.automation.di

import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.LogcatAutomationLogger
import com.tomtruyen.automation.data.AutomationRuleRepository
import com.tomtruyen.automation.data.AutomationRuleRepositoryImpl
import com.tomtruyen.automation.features.actions.ActionExecutor
import com.tomtruyen.automation.features.constraints.ConstraintEvaluator
import com.tomtruyen.automation.features.triggers.TriggerMatcher
import org.koin.dsl.module

val automationModule = module {
    // Repository
    single<AutomationRuleRepository> { AutomationRuleRepositoryImpl() }

    // Logger
    single<AutomationLogger> { LogcatAutomationLogger() }

    // Runtime Service
    single<AutomationRuntimeService> {
        AutomationRuntimeService(
            repository = get(),
            // TODO: Replace delegates with dynamic delegate?
            //   => Automatically register all TriggerDelegate types somehow?
            triggerMatcher = TriggerMatcher(emptyMap()),
            constraintEvaluator = ConstraintEvaluator(emptyMap()),
            actionExecutor = ActionExecutor(emptyMap()),
        )
    }

}
