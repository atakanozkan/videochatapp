buildscript {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
    dependencies {
        classpath(libs.google.services)
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}