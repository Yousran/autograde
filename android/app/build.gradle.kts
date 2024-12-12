plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.autograde"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.autograde"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true

    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.ui.desktop)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.lifecycle.viewmodel.ktx)
    implementation(libs.datastore.preferences)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.glide)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.fragment.ktx)
    ksp(libs.room.compiler)
    kapt(libs.glide.compiler)
    implementation(libs.androidx.work.runtime)
    implementation(libs.android.async.http)
    implementation (libs.work.runtime.ktx)
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)

}