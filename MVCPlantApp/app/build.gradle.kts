import com.android.build.gradle.internal.cxx.configure.abiOf

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")

    kotlin("plugin.serialization") version "1.9.23"
}

android {
    namespace = "com.example.mvcplantapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mvcplantapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
            abiFilters.add("x86")
            abiFilters.add("x86_64")
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //adding viewmodel dependency
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx")

    //para que vaya el viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

    //para que las vistas puedan observar objetos del viewModel y actualizarse
    implementation("androidx.lifecycle:lifecycle-livedata-ktx")






    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("com.google.firebase:firebase-storage-ktx")


    //to draw images
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("io.coil-kt:coil")

    //supabase
    implementation("io.github.jan-tennert.supabase:storage-kt:3.0.3")
    implementation("io.ktor:ktor-client-android:3.0.3")


}