plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "ru.blays.revanced.installer"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.kotlinx.kotlinx.coroutines.core.jvm)

    // LibSu
    implementation(libs.core)
    implementation(libs.service)
    implementation(libs.nio)

    implementation(projects.shared)

}