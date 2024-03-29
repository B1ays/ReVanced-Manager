plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "ru.blays.revanced.data"
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

    //AndroidX
    implementation(libs.androidx.core.core.ktx)

    // KotlinX Serialization
    implementation(libs.kotlinx.kotlinx.serialization.core.jvm)
    implementation(libs.kotlinx.kotlinx.serialization.json.jvm)

    // OkHttp
    implementation(libs.com.squareup.okhttp3.okhttp)

    // Room
    implementation(libs.androidx.room.room.runtime)
    implementation(libs.androidx.room.room.ktx)
    annotationProcessor(libs.androidx.room.room.compiler)
    ksp(libs.androidx.room.room.compiler)

    // Koin
    implementation(libs.koin.android)
    
    // Modules
    implementation(projects.domain)
    implementation(projects.shared)
}