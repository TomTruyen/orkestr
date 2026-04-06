import com.android.build.api.dsl.LibraryExtension
import orkestr.libs

val libs = project.libs

apply(plugin = "com.android.compose.screenshot")

extensions.configure<LibraryExtension> {
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    add("screenshotTestImplementation", platform(libs.findLibrary("androidx.compose.bom").get()))
    add("screenshotTestImplementation", libs.findLibrary("screenshot.validation.api").get())
    add("screenshotTestImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
}
