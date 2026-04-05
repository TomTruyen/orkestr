plugins {
    id("org.jetbrains.kotlin.jvm")
    id("orkestr.detekt")
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}
