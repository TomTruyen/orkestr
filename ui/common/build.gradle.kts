plugins {
    id("orkestr.android.library")
    alias(libs.plugins.kotlin.compose)
    id("orkestr.android.compose")
    id("orkestr.koin.android")
}

android {
    namespace = "com.tomtruyen.orkestr.ui.common"
}

dependencies {
    implementation(project(":automation"))
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
