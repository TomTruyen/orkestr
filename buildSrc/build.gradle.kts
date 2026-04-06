plugins {
    `kotlin-dsl`
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.compose.screenshot) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation("com.android.compose.screenshot:com.android.compose.screenshot.gradle.plugin:${libs.versions.screenshot.get()}")
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
}
