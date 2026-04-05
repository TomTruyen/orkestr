import com.android.build.api.dsl.LibraryExtension
import orkestr.libs
import orkestr.projectJavaVersion
import orkestr.versionInt

apply(plugin = "com.android.library")
apply(plugin = "orkestr.detekt")

val libs = project.libs

extensions.configure<LibraryExtension> {
    compileSdk {
        version = release(libs.versionInt("androidCompileSdk")) {
            minorApiLevel = libs.versionInt("androidCompileSdkMinor")
        }
    }

    defaultConfig {
        minSdk = libs.versionInt("androidMinSdk")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = project.projectJavaVersion()
        targetCompatibility = project.projectJavaVersion()
    }
}
