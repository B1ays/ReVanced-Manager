plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

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
        kotlinCompilerExtensionVersion = "1.4.8"
    }
}

dependencies {

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)

    // Compose
    implementation(libs.androidx.compose.runtime)

    // Helios
    /*implementation(projects.heliosCore)*/
    implementation(project(":Helios:helios-core"))

    /*implementation(Dependencies.ktx)
    implementation(Dependencies.stdLib)
    implementation(Dependencies.composeRuntime)

    api(project(":core"))*/
}