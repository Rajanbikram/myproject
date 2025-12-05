pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // ... other plugins
        // Add this line (use the version that matches your Kotlin version, 2.0.0 or 2.1.0)
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // Also ensure it's here for library dependencies
        mavenCentral()
    }
}


rootProject.name = "myproject"
include(":app")
