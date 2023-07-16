plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "ru.blays.helios.navigator.bottomSheet"
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.ui.ui.tooling)

    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.compose)

    // Helios
   /* implementation(projects.heliosCore)
    implementation(projects.heliosNavigator)*/
    implementation(project(":Helios:helios-core"))
    implementation(project(":Helios:helios-navigator"))

   /* implementation(Dependencies.ktx)
    implementation(Dependencies.stdLib)
    implementation(Dependencies.composeRuntime)

    implementation(Dependencies.composeActivity)
    implementation(Dependencies.composeUi)
    implementation(Dependencies.composeMaterial)
    implementation(Dependencies.composeFoundation)
    implementation(Dependencies.composeLifecycleRuntime)
    implementation(Dependencies.composeTooling)

    api(project(":core"))
    api(project(":navigator"))*/
}