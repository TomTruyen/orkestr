import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import orkestr.libs

val libs = project.libs

pluginManager.withPlugin("com.android.application") {
    extensions.configure<ApplicationExtension> {
        buildFeatures.compose = true
    }
}

pluginManager.withPlugin("com.android.library") {
    extensions.configure<LibraryExtension> {
        buildFeatures.compose = true
    }
}

dependencies {
    add("implementation", libs.findLibrary("androidx.core.ktx").get())
    add("implementation", libs.findLibrary("androidx.activity.compose").get())
    add("implementation", platform(libs.findLibrary("androidx.compose.bom").get()))
    add("implementation", libs.findLibrary("androidx.compose.animation").get())
    add("implementation", libs.findLibrary("androidx.compose.ui").get())
    add("implementation", libs.findLibrary("androidx.compose.ui.graphics").get())
    add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
    add("implementation", libs.findLibrary("androidx.compose.material3").get())
    add("implementation", libs.findLibrary("androidx.compose.material.icons.extended").get())
}
