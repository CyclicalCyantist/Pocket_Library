plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.recycleview_simple"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.recycleview_simple"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    // --- Core Android & UI Libraries ---
    // Using the newer versions you have listed or stable ones.
    implementation("androidx.core:core-ktx:1.13.1") // Updated from 1.7.0 for better compatibility
    implementation("androidx.appcompat:appcompat:1.7.0") // Updated from 1.4.1
    implementation("com.google.android.material:material:1.12.0") // Updated from 1.5.0
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Updated from 2.1.3
    implementation("androidx.activity:activity-ktx:1.9.1") // Common dependency for modern apps

    // --- Navigation ---
    // Using a stable version. Your 2.9.4 version doesn't exist. The latest stable is 2.7.x
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // --- Lifecycle & ViewModel ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4") // Kept your newer version
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4") // Kept your compose version

    // --- Networking: Retrofit & Moshi (Consolidated) ---
    // You had two versions of Retrofit (2.9.0 and 3.0.0). 3.0.0 is a pre-release snapshot.
    // Stick with the latest stable version: 2.11.0.
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1") // Kept your version
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Updated from 4.11.0

    // --- Coroutines ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // --- Database: Room ---
    // This is the source of the kapt task. Version 2.6.1 is correct.
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // --- Image Loading for Compose ---
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- Testing ---
    testImplementation(libs.junit) // Assuming this is defined in your libs.versions.toml
    androidTestImplementation(libs.androidx.junit) // Assuming this is defined
    androidTestImplementation(libs.androidx.espresso.core) // Assuming this is defined
}