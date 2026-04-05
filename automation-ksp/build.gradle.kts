plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(project(":automation-ksp-annotations"))
    implementation(libs.ksp.symbol.processing.api)
}
