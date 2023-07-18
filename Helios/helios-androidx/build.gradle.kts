plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

val composeCompiler: String by extra { "ComposeCompiler" }

android {
    namespace = "ru.blays.helios.androidx"
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

    // Helios
    implementation(projects.helios.heliosCore)

}