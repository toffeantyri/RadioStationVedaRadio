import java.util.Properties

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.ksp) apply false

    alias(libs.plugins.navigation.safe.args) apply false
    alias(libs.plugins.firebase.crashlytics.gradle) apply false
    alias(libs.plugins.google.services) apply false
}

buildscript {

    repositories {
        google()

    }
    dependencies {
        classpath(libs.navigation.safe.args)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.google.services)
    }

}

true