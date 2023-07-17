plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "ru.blays.helios.navigator"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        release {
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
    }

}

dependencies {

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)

    // Compose
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.activity.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Helios
    implementation(project(":Helios:helios-core"))

}