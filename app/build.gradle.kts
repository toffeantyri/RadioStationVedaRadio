import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)

    alias(libs.plugins.navigation.safe.args)

    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics.gradle)
}

android {
    namespace = "ru.music.radiostationvedaradio"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.music.radiostationvedaradio"
        minSdk = 26
        targetSdk = 34
        versionCode = 7
        versionName = "1.1"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            versionNameSuffix = ".debug"
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs += listOf(
            "-Xno-call-assertions",
            "-Xno-receiver-assertions",
            "-Xno-param-assertions"
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    //yandex ads
    implementation("com.yandex.android:mobileads:5.1.1")
    implementation("com.yandex.android:mobmetricalib:4.1.1")


    // Mini equalizer
    implementation("com.github.claucookie.miniequalizer:library:1.0.0")

//di
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)

//mvvm
    implementation(libs.lifecycle.livedata.ktx)

//network
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.login.interceptor)

    //retrofit
    implementation("org.simpleframework:simple-xml:2.7.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    //jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")

    //add dependency for new sdk new android version
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")


    //base view
    implementation(libs.ya.map)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraint.layout)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    //db
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.work.runtime.ktx)
    ksp(libs.room.compiler)

    //navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    // Feature module Support
    implementation(libs.navigation.dynamic.features.fragment)

    //image
    implementation(libs.coil)

    //skeletons and shimmering effects
    implementation(libs.androidveil)

    implementation(platform(libs.firebase.bom))
    // Add the dependencies for the Crashlytics and Analytics libraries
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}