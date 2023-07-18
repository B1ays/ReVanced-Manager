plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

val composeCompiler: String by extra { "ComposeCompiler" }

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
        kotlinCompilerExtensionVersion = composeCompiler
    }

}

dependencies {

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.kotlin.kotlin.stdlib)

    // Compose
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.activity.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Helios
    implementation(project(":Helios:helios-core"))

}