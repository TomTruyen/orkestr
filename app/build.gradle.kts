import java.util.Properties

plugins {
    id("orkestr.android.application")
    alias(libs.plugins.kotlin.compose)
    id("orkestr.android.compose")
    id("orkestr.koin.android")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tomtruyen.orkestr"

    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use(::load)
        }
    }
    val googleMapsApiKey = providers.environmentVariable("GOOGLE_MAPS_API_KEY").orNull
        ?: localProperties.getProperty("googleMapsApiKey", "")

    defaultConfig {
        applicationId = "com.tomtruyen.orkestr"
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["googleMapsApiKey"] = googleMapsApiKey
    }
}

dependencies {
    implementation(project(":automation"))
    implementation(project(":ui:automation"))
    implementation(project(":ui:common"))
    implementation(project(":ui:geofence"))
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
