plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.rsetiapp"  // ✅ Ensure this matches your package name
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
        debug {
            isMinifyEnabled = false
            buildConfigField("String", "API_KEY", "\"DEBUG_API_KEY\"")  // ✅ Example BuildConfig variable
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true  // ✅ Ensure BuildConfig is enabled
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
}

kapt {
    correctErrorTypes = true
}
