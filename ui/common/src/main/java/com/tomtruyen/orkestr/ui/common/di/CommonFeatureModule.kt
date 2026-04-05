package com.tomtruyen.orkestr.ui.common.di

import com.tomtruyen.orkestr.common.AndroidStringResolver
import com.tomtruyen.orkestr.common.StringResolver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonFeatureModule = module {
    single<StringResolver> { AndroidStringResolver(androidContext()) }
}
