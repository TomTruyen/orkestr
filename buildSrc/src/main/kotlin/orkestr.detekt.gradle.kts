import dev.detekt.gradle.extensions.DetektExtension
import orkestr.libs
import orkestr.version
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

apply(plugin = "dev.detekt")

val libs = project.libs
val javaTarget = libs.version("java")

extensions.configure<DetektExtension> {
    toolVersion = libs.version("detekt")
    config.setFrom(rootProject.file("detekt.yml"))
    buildUponDefaultConfig = true
    parallel = true
    autoCorrect = true
    ignoreFailures = false
    basePath.set(rootDir)
}

dependencies {
    add("detektPlugins", libs.findLibrary("detekt-formatting").get())
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    jvmTarget.set(javaTarget)
    reports {
        html.required.set(true)
        sarif.required.set(true)
    }
}

tasks.withType<dev.detekt.gradle.DetektCreateBaselineTask>().configureEach {
    jvmTarget.set(javaTarget)
}
