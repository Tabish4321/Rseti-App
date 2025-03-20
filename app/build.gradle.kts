plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.rsetiapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rsetiapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // ✅ Ensure this is correctly set for Android instrumented tests
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
        }
        create("prod") {
            dimension = "app"
        }
        create("stag") {
            dimension = "app"
        }
    }


}

dependencies {
    // ✅ Core Android Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.core)
    implementation (libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)

    // ✅ Lifecycle Components
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // ✅ Navigation Components
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment.ktx)

    // ✅ Room Database
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // ✅ DataStore
    implementation(libs.androidx.datastore.preferences)

    // ✅ Retrofit and Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // ✅ Glide (Image Loading)
    implementation(libs.glide)
    kapt(libs.compiler)

    // ✅ Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // ✅ Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // ✅ Permissions Handling (Dexter)
    implementation(libs.dexter)

    // ✅ Security Libraries
    implementation(libs.bcprov.jdk16)
    implementation(libs.jsr105.api)

    // ✅ Retrofit Converters
    implementation(libs.converter.scalars)
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // ✅ OkHttp for Networking
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // ✅ **Test Dependencies (Placed in Correct Category)**
    // 🟢 **Unit Testing (Runs on JVM, No Android Emulator Needed)**
    testImplementation(libs.junit)

    // 🟢 **Android Instrumented Tests (Runs on Android Device/Emulator)**
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Xml
    implementation(libs.jackson.dataformat.xml)
    implementation("com.thoughtworks.xstream:xstream:1.4.7") {
        exclude(group = "xmlpull", module = "xmlpull")
    }

    implementation(libs.bcprov.jdk16)
    implementation(libs.jsr105.api)
    implementation("org.apache.santuario:xmlsec:2.0.3") {
        exclude(group = "org.codehaus.woodstox")
    }

        implementation ("com.fasterxml.jackson.core:jackson-core:2.15.0")
        implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.0")
        implementation ("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.0")

        // Add StAX API( (Required for XML Parsing)
        implementation ("javax.xml.stream:stax-api:1.0-2")
        implementation ("com.fasterxml.woodstox:woodstox-core:6.5.1")


}

kapt {
    correctErrorTypes = true
}
