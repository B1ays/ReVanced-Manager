plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "ru.blays.downloader"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
}

dependencies {
    // AndroidX
    implementation(libs.androidx.core.core.ktx)

    // KotlinX
    implementation(libs.kotlinx.kotlinx.coroutines.core.jvm)

    // Kotlin reflection
    implementation(libs.kotlin.reflect)

    // OkHttp
    implementation(libs.com.squareup.okhttp3.okhttp)

    // Pausing coroutines
    implementation(libs.pausing.coroutine.dispatcher)

    // Project
    implementation(projects.simpleDocument)
}