// build.gradle.kts (Project-level: <your-project-name>/build.gradle.kts)
// This file makes the plugins defined in libs.versions.toml available to sub-modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleServices) apply false
}

