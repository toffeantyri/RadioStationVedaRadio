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
    implementation(libs.yandex.ads)
    implementation(libs.yandex.metr)


    // Mini equalizer
    implementation(libs.miniequalizer)

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
    implementation(libs.simple.xml)
    implementation(libs.converter.gson)
    implementation(libs.converter.simplexml)
    implementation(libs.converter.scalars)

    //jackson
    implementation(libs.jackson.module.kotlin)

    //add dependency for new sdk new android version
    implementation(libs.gson)


    //base view
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

    implementation(platform(libs.firebase.bom))
    // Add the dependencies for the Crashlytics and Analytics libraries
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    //splash screen
    implementation(libs.core.splashscreen)

}