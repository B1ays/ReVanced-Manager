@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "ru.blays.helios.dialogs"
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
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}

dependencies {

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.kotlin.kotlin.stdlib)

    // Compose
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.activity.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.ui.ui.tooling)

    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.compose)

    // Helios
    implementation(projects.helios.heliosCore)
    implementation(projects.helios.heliosNavigator)
}