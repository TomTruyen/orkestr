plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
}

kotlin {
    jvmToolchain(11)
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(rootProject.file("detekt.yml"))
    buildUponDefaultConfig = true
    parallel = true
    autoCorrect = true
    ignoreFailures = false
    basePath.set(rootDir)
}

dependencies {
    implementation(project(":automation-ksp-annotations"))
    implementation(libs.ksp.symbol.processing.api)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    detektPlugins(libs.detekt.formatting)
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    jvmTarget.set("11")
    reports {
        html.required.set(true)
        sarif.required.set(true)
    }
}

tasks.withType<dev.detekt.gradle.DetektCreateBaselineTask>().configureEach {
    jvmTarget.set("11")
}
