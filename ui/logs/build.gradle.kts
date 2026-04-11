plugins {
    id("orkestr.android.library")
    alias(libs.plugins.kotlin.compose)
    id("orkestr.android.compose")
    id("orkestr.android.compose.screenshot")
    id("orkestr.koin.android")
}

android {
    namespace = "com.tomtruyen.orkestr.ui.logs"
}

dependencies {
    implementation(project(":automation"))
    implementation(project(":ui:common"))
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.paging.compose)
    testImplementation(libs.junit)
}
