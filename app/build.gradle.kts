plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize") // Add this line


}

android {
    namespace = "com.rsetiapp"  // ✅ Ensure this matches your package name
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rsetiapp"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // ✅ Ensure this is correctly set for Android instrumented tests
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ Correct Kotlin DSL syntax for keeping all language resources
        resourceConfigurations += listOf("en", "hi", "as", "bn", "gu", "kn", "ml", "mr", "or", "pa", "ta", "te", "ur")



    }

    // ✅ Prevent Google Play from splitting languages (needed for in-app switching)
    bundle {
        language {
            enableSplit = false
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            buildConfigField("String", "ENCRYPT_IV_KEY", "\"$10A80$10A80$10A\"")  // ✅ Example BuildConfig variable
            buildConfigField("String", "ENCRYPT_KEY", "\"$10A80$10A80$10A\"")  // ✅ Example BuildConfig variable
            buildConfigField("String", "CRYPLIBAES", "\"AES/CBC/PKCS5PADDING\"")  // ✅ Example BuildConfig variable
            buildConfigField("String", "CRYPT_ID", "\"8080808080808080\"")  // ✅ Example BuildConfig variable
            buildConfigField("String", "CRYPT_IV", "\"8080808080808080\"")  // ✅ Example BuildConfig variable

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            buildConfigField("String", "API_KEY", "\"DEBUG_API_KEY\"")
            buildConfigField("String", "ENCRYPT_IV_KEY", "\"$10A80$10A80$10A\"")
            buildConfigField("String", "ENCRYPT_KEY", "\"$10A80$10A80$10A\"")
            buildConfigField("String", "CRYPLIBAES", "\"AES/CBC/PKCS5PADDING\"")
            buildConfigField("String", "CRYPT_ID", "\"8080808080808080\"")
            buildConfigField("String", "CRYPT_IV", "\"8080808080808080\"")




        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true  // ✅ Ensure BuildConfig is enabled
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    flavorDimensions += listOf("app")

    productFlavors {
        create("dev") {
            dimension = "app"
            buildConfigField("String", "BASE_URL", "\"https://dev.example.com/\"")  // ✅ Add BuildConfig values
        }
        create("prod") {
            dimension = "app"
            buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")
        }
        create("stag") {
            dimension = "app"
            buildConfigField("String", "BASE_URL", "\"https://staging.example.com/\"")
        }
    }
/*
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }*/
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.core)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.glide)
    kapt(libs.compiler)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.dexter)

    implementation(libs.bcprov.jdk16)
    implementation(libs.jsr105.api)

    implementation(libs.converter.scalars)
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.jackson.dataformat.xml)
    implementation("com.thoughtworks.xstream:xstream:1.4.7") {
        exclude(group = "xmlpull", module = "xmlpull")
    }

    implementation(libs.bcprov.jdk16)
    implementation(libs.jsr105.api)
    implementation("org.apache.santuario:xmlsec:2.0.3") {
        exclude(group = "org.codehaus.woodstox")
    }

    implementation("com.fasterxml.jackson.core:jackson-core:2.15.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.0")

    implementation("javax.xml.stream:stax-api:1.0-2")
    implementation("com.fasterxml.woodstox:woodstox-core:6.5.1")

    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation(libs.androidx.activity.compose)


/*

    // Pehchaan SDK

// Jetpack Compose (if you're using Compose)
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

// CardView
    implementation(libs.androidx.cardview)
// WorkManager
    implementation("androidx.work:work-runtime-ktx:2.10.0")

// Transport runtime (used by Firebase and others)
    implementation("com.google.android.datatransport:transport-runtime:2.2.6")

// ML Kit (Vision)
    implementation("com.google.mlkit:face-detection:16.1.7")
    implementation("com.google.mlkit:vision-common:16.1.7")
    implementation(libs.play.services.vision)

// CameraX
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

// SweetAlert Dialog
    implementation(libs.library)

// SDP & SSP for responsive dimensions
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

// Kotlin Coroutines for Play Services
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0-RC")

// Koin (Dependency Injection)
    implementation("io.insert-koin:koin-android:3.5.6")
    implementation("io.insert-koin:koin-annotations:1.3.1")
    implementation("io.insert-koin:koin-androidx-compose:3.5.6")
// ksp("io.insert-koin:koin-ksp-compiler:1.3.0") // Only if you're using KSP

// TensorFlow Lite
    implementation ("com.google.ai.edge.litert:litert:1.1.2")
    implementation ("com.google.ai.edge.litert:litert-gpu:1.1.2")
    implementation ("com.google.ai.edge.litert:litert-gpu-api:1.1.2")
    implementation ("com.google.ai.edge.litert:litert-support:1.2.0")

// MediaPipe Tasks Vision
    implementation("com.google.mediapipe:tasks-vision:0.10.14")

*/

}

kapt {
    correctErrorTypes = true
}


/*// Pehchaan SDK
implementation(files("libs/pehchaanlib.aar"))

// Jetpack Compose (if you're using Compose)
implementation("androidx.activity:activity-compose:1.8.2")
implementation(platform("androidx.compose:compose-bom:2024.02.02"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3:1.2.1")
androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.02"))
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
debugImplementation("androidx.compose.ui:ui-tooling")
debugImplementation("androidx.compose.ui:ui-test-manifest")

// CardView
implementation("androidx.cardview:cardview:1.0.0")

// WorkManager
implementation("androidx.work:work-runtime-ktx:2.9.0")

// Transport runtime (used by Firebase and others)
implementation("com.google.android.datatransport:transport-runtime:3.1.8")

// ML Kit (Vision)
implementation("com.google.mlkit:face-detection:17.1.0")
implementation("com.google.mlkit:vision-common:17.3.0")
implementation("com.google.android.gms:play-services-vision:20.1.3")

// CameraX
implementation("androidx.camera:camera-camera2:1.3.1")
implementation("androidx.camera:camera-lifecycle:1.3.1")
implementation("androidx.camera:camera-view:1.3.1")

// SweetAlert Dialog
implementation("com.github.f0ris.sweetalert:library:1.5.6")

// SDP & SSP for responsive dimensions
implementation("com.intuit.sdp:sdp-android:1.1.0")
implementation("com.intuit.ssp:ssp-android:1.1.0")

// Kotlin Coroutines for Play Services
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

// Koin (Dependency Injection)
implementation("io.insert-koin:koin-android:3.5.3")
implementation("io.insert-koin:koin-annotations:1.3.0")
implementation("io.insert-koin:koin-androidx-compose:3.5.3")
// ksp("io.insert-koin:koin-ksp-compiler:1.3.0") // Only if you're using KSP

// TensorFlow Lite
    implementation ("com.google.ai.edge.litert:litert:1.1.2")
    implementation ("com.google.ai.edge.litert:litert-gpu:1.1.2")
    implementation ("com.google.ai.edge.litert:litert-gpu-api:1.1.2")
    implementation ("com.google.ai.edge.litert:litert-support:1.2.0")

// MediaPipe Tasks Vision
implementation("com.google.mediapipe:tasks-vision:0.10.11")
*/