plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

    namespace = "com.phonebridge"

    compileSdk = 35

    defaultConfig {

        applicationId = "com.phonebridge"

        minSdk = 26

        targetSdk = 32

        versionCode = 1

        versionName = "0.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
