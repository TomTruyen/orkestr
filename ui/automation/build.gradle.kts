plugins {
    id("orkestr.android.library")
    alias(libs.plugins.kotlin.compose)
    id("orkestr.android.compose")
    id("orkestr.android.compose.screenshot")
    id("orkestr.koin.android")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tomtruyen.orkestr.ui.automation"
}

dependencies {
    implementation(project(":automation"))
    implementation(project(":ui:common"))
    implementation(project(":ui:geofence"))
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
