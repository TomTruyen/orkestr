package com.tomtruyen.orkestr.features.logs.di

import com.tomtruyen.orkestr.features.logs.viewmodel.AutomationLogsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val logsFeatureModule = module {
    viewModel {
        AutomationLogsViewModel(
            repository = get(),
        )
    }
}
