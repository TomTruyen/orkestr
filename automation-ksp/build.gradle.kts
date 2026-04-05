plugins {
    id("org.jetbrains.kotlin.jvm")
    id("orkestr.detekt")
}

dependencies {
    implementation(project(":automation-ksp-annotations"))
    implementation(libs.ksp.symbol.processing.api)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}
