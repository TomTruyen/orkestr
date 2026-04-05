package com.tomtruyen.orkestr.features.geofence.di

import com.tomtruyen.automation.data.repository.GeofenceRepository
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.geofence.data.GeofenceLocationRepository
import com.tomtruyen.orkestr.features.geofence.data.GeofenceSearchRepository
import com.tomtruyen.orkestr.features.geofence.viewmodel.GeofenceTriggerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val geofenceFeatureModule = module {
    single { GeofenceLocationRepository(androidContext()) }
    single { GeofenceSearchRepository(androidContext()) }

    viewModel {
        GeofenceTriggerViewModel(
            stringResolver = get<StringResolver>(),
            geofenceRepository = get<GeofenceRepository>(),
            geofenceLocationRepository = get<GeofenceLocationRepository>(),
            geofenceSearchRepository = get<GeofenceSearchRepository>(),
        )
    }
}
