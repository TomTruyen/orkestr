plugins {
    id("orkestr.android.library")
    alias(libs.plugins.kotlin.compose)
    id("orkestr.android.compose")
    id("orkestr.koin.android")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tomtruyen.orkestr.ui.geofence"
}

dependencies {
    implementation(project(":automation"))
    implementation(project(":ui:common"))
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.google.maps.compose)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
