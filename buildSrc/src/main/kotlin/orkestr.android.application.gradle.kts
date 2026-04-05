import com.android.build.api.dsl.ApplicationExtension
import orkestr.libs
import orkestr.projectJavaVersion
import orkestr.versionInt

apply(plugin = "com.android.application")
apply(plugin = "orkestr.detekt")

val libs = project.libs

extensions.configure<ApplicationExtension> {
    compileSdk {
        version = release(libs.versionInt("androidCompileSdk")) {
            minorApiLevel = libs.versionInt("androidCompileSdkMinor")
        }
    }

    defaultConfig {
        minSdk = libs.versionInt("androidMinSdk")
        targetSdk = libs.versionInt("androidTargetSdk")
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
