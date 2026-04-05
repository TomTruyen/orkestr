pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "orkestr"
include(":app")
include(":automation")
include(":automation-ksp")
include(":automation-ksp-annotations")
include(":ui:common")
include(":ui:automation")
include(":ui:geofence")

project(":ui:common").projectDir = file("ui/common")
project(":ui:automation").projectDir = file("ui/automation")
project(":ui:geofence").projectDir = file("ui/geofence")
