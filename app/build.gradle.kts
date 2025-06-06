plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.carsellingshop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.carsellingshop"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {
    implementation(libs.appcompat.get())
    implementation(libs.material.get())
    implementation(libs.activity.get())
    implementation(libs.constraintlayout.get())
    implementation("androidx.recyclerview:recyclerview:1.3.2") // For car listings
    implementation("com.github.bumptech.glide:glide:4.16.0") // For loading car images
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0") // Required for Glide
    implementation("org.mindrot:jbcrypt:0.4") // For password hashing
    testImplementation(libs.junit.get())
    androidTestImplementation(libs.ext.junit.get())
    androidTestImplementation(libs.espresso.core.get())
}