plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "ru.blays.revanced.Presentation"
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}

dependencies {

    // AndroidX
    implementation(libs.androidx.core.asProvider())

    //Compose
    implementation(libs.androidx.activity.activity.compose)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.asProvider())
    implementation(libs.androidx.compose.ui.asProvider())

    // Compose Markdown
    implementation(libs.compose.markdown)

    // Coil
    implementation(libs.coil)

    // Color picker compose
    implementation(libs.colorpicker.compose)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Rebugger
    implementation(libs.rebugger)

    // Modules
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.deviceUtils)
    implementation(projects.shared)

    // material-color-utilities
    implementation(files("libs/material-color-util.jar"))
}