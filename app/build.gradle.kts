plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.proyectofinalam"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.proyectofinalam"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    ndkVersion = "26.1.10909125"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dependencias de Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.5.0")) // BOM
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-functions-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Dependencias de Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Dependencia de OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.0")

    // Dependencia de Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation ("androidx.appcompat:appcompat:1.6.1") // O la última versión disponible
    implementation ("com.google.android.material:material:1.9.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0") // Si usas Kotlin

    // Dependencias de JavaMail
    implementation ("com.sun.mail:android-mail:1.6.2")
    implementation ("com.sun.mail:android-activation:1.6.2")

    // Dependencia de Firebase Cloud Messaging (FCM)
    implementation ("com.google.firebase:firebase-messaging:23.1.0")

    // Dependencia de OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.0")

    //Dependecia de Carrusel
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
}
