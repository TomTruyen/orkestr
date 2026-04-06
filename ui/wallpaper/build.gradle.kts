plugins {
    id("orkestr.android.library")
    alias(libs.plugins.kotlin.compose)
    id("orkestr.android.compose")
    id("orkestr.android.compose.screenshot")
}

android {
    namespace = "com.tomtruyen.orkestr.ui.wallpaper"
}

dependencies {
    implementation(project(":automation"))
    implementation(project(":ui:common"))
}
