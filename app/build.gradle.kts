plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.kotlinAndroid)
}


android {
    namespace = "ru.Blays.ReVanced.Manager"
    compileSdk = 33
    compileSdkPreview = "UpsideDownCake"

    defaultConfig {
        applicationId = "ru.Blays.ReVanced.Manager"
        minSdk = 26
        targetSdk = 33
        versionName = "1.0.5 beta"
        versionCode = 6

        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )

        resourceConfigurations += arrayOf("en", "ru")

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {

            isMinifyEnabled = true
            isShrinkResources = true
            isCrunchPngs = true
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        checkReleaseBuilds = false
        checkDependencies = false
        checkGeneratedSources = false
        abortOnError = false
    }
}

dependencies {

    // AndroidX
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)

    // Work manager
    implementation(libs.androidx.work.runtime.ktx)

    // Compose
    implementation(libs.androidx.activity.activity.compose)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)

    // Accompanist
    implementation(libs.accompanist.permissions)

    // Rebugger
    implementation(libs.rebugger)

    // Color picker compose
    implementation(libs.colorpicker.compose)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // LibSu
    implementation(libs.core)
    implementation(libs.service)
    implementation(libs.nio)

    // OkHttp
    implementation(libs.com.squareup.okhttp3.okhttp)

    // Helios
    implementation(projects.helios.heliosCore)
    implementation(projects.helios.heliosNavigator)
    implementation(projects.helios.heliosBottomSheetNavigator)
    implementation(projects.helios.heliosDialogs)
    implementation(projects.helios.heliosAndroidx)
    implementation(projects.helios.heliosTransitions)

    // Modules
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.deviceUtils)
    implementation(projects.composeElements)
    implementation(projects.shared)
    implementation(projects.preference)
    implementation(projects.downloader)
    implementation(projects.simpleDocument)

}