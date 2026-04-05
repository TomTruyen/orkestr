plugins {
    id("orkestr.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.tomtruyen.automation"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":automation-ksp-annotations"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.material)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.android)
    implementation(libs.play.services.location)
    ksp(project(":automation-ksp"))
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
