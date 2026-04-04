package com.tomtruyen.orkestr.di

import com.tomtruyen.automation.data.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRulesViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        AutomationRulesViewModel(
            repository = get<AutomationRuleRepository>()
        )
    }

    viewModel {
        AutomationRuleEditorViewModel(
            context = androidContext(),
            repository = get<AutomationRuleRepository>(),
            definitions = get<AutomationDefinitionRegistry>()
        )
    }
}
