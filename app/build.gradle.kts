plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.finsmart"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.finsmart"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Включаем core library desugaring
        multiDexEnabled = true
    }

    // 🔥 Включаем поддержку java.time через desugaring
    buildFeatures {
        viewBinding = true // если нужно
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Поддержка java.time для Android < API 26
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
}