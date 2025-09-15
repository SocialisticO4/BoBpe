import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.phonepe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.phonepe"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load the apikeys.properties file
        val apikeyPropertiesFile = rootProject.file("apikeys.properties")
        val apikeyProperties = Properties()
        
        if (apikeyPropertiesFile.exists()) {
            apikeyProperties.load(FileInputStream(apikeyPropertiesFile))
        }
        
        // Add keys to BuildConfig
        // Ensure that your apikeys.properties file has these keys defined, otherwise they will be null.
        // If a key might be missing, you can provide a default value or handle null appropriately.
        buildConfigField("String", "API_KEY", "\"${apikeyProperties.getProperty("API_KEY", "YOUR_DEFAULT_API_KEY_IF_ANY")}\"")
        buildConfigField("String", "BASE_URL", "\"${apikeyProperties.getProperty("BASE_URL", "YOUR_DEFAULT_BASE_URL_IF_ANY")}\"")
        buildConfigField("String", "SECRET_TOKEN", "\"${apikeyProperties.getProperty("SECRET_TOKEN", "YOUR_DEFAULT_SECRET_TOKEN_IF_ANY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Text APIs (KeyboardOptions, etc.)
    implementation("androidx.compose.ui:ui-text")
    // Foundation APIs (KeyboardOptions provider and more)
    implementation("androidx.compose.foundation:foundation")
    // Material Icons Extended for QR and other icons
    implementation("androidx.compose.material:material-icons-extended")

    // Lifecycle Compose for LocalLifecycleOwner, etc.
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Google Play Services Code Scanner (replaces CameraX + ML Kit in-app libs)
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

    // Room for Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // NeoPop by CRED (Maven artifact) for PopFrameLayout
    implementation("club.cred:neopop:1.0.2")

    implementation("androidx.core:core-splashscreen:1.0.1")

    // CameraX for in-app camera preview and image analysis
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")

    // ML Kit Barcode Scanning for QR parsing
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}