plugins {
    id("orkestr.android.library")
    alias(libs.plugins.kotlin.compose)
    id("orkestr.android.compose")
    id("orkestr.koin.android")
}

android {
    namespace = "com.tomtruyen.orkestr.ui.wifi"
}

dependencies {
    implementation(project(":automation"))
    implementation(project(":ui:common"))
    testImplementation(libs.junit)
}
