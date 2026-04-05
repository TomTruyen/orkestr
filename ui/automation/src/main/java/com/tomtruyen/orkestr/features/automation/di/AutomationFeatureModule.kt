package com.tomtruyen.orkestr.features.automation.di

import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRulesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val automationFeatureModule = module {
    viewModel {
        AutomationRulesViewModel(
            repository = get<AutomationRuleRepository>(),
        )
    }

    viewModel {
        AutomationRuleEditorViewModel(
            stringResolver = get<StringResolver>(),
            repository = get<AutomationRuleRepository>(),
            definitions = get<AutomationDefinitionRegistry>(),
        )
    }
}
