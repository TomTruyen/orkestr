plugins {
    id("orkestr.android.library")
    alias(libs.plugins.kotlin.compose)
    id("orkestr.android.compose")
}

android {
    namespace = "com.tomtruyen.orkestr.ui.timebased"
}

dependencies {
    implementation(project(":automation"))
    implementation(project(":ui:common"))
}
