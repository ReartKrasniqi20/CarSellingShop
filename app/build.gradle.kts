plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
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
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("org.mindrot:jbcrypt:0.4")

    // Firebase
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-firestore:24.10.0")

    testImplementation(libs.junit.get())
    androidTestImplementation(libs.ext.junit.get())
    androidTestImplementation(libs.espresso.core.get())

}
