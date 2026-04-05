plugins {
    `kotlin-dsl`
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.detekt) apply false
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
}
