// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.6.0"
}

buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)

    }
}