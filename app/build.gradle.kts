plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.example.kaleidoscope"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kaleidoscope"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":shared"))
    
    // Android core dependencies
    // SDK Extensions for runtime API checks
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    // Google Play Services (Base)
    implementation("com.google.android.gms:play-services-base:18.4.0")
    // Google Play Services Auth (for authentication, optional)
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    // Firebase Analytics (optional, for analytics)
    implementation("com.google.firebase:firebase-analytics-ktx:21.5.0")
    // Google Play Billing for subscriptions
    implementation("com.android.billingclient:billing-ktx:6.2.1")

    // Google Play Services (Base)
    implementation("com.google.android.gms:play-services-base:18.4.0")
    // Google Play Services Auth (for authentication, optional)
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    // Google Play Core (for in-app updates, reviews, etc.)
    implementation("com.google.android.play:core:1.10.3")
    // Google Play Core KTX (Kotlin extensions)
    implementation("com.google.android.play:core-ktx:1.8.1")
    
    // Jetpack Compose BOM for version alignment
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.7.5")
    // Tooling
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // SceneView for 3D visualization
    implementation("io.github.sceneview:sceneview:1.0.3")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    
    // Charts and visualization
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}