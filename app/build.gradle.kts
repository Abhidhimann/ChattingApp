import java.util.Properties

// Load local.properties file
val localProperties = Properties()
val localPropertiesFile = rootProject.file("project.properties")
localProperties.load(localPropertiesFile.inputStream())

// Access properties from local.properties
val webClientId: String = localProperties.getProperty("WEB_CLIENT_ID")
val aiToken: String = localProperties.getProperty("AI_TOKEN")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.example.chattingApp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chattingApp"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${webClientId}\"")
        buildConfigField("String", "AI_TOKEN", "\"${aiToken}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            // minify not working will see why todo
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.9.0")

    // navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // constraint layout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore:24.0.0")

    // task to coroutines because i like coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.4.1")

    // ser
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Logging Interceptor for debugging
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Gson
    implementation("com.squareup.retrofit2:converter-scalars:2.1.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // shared preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // extended icons
    implementation("androidx.compose.material:material-icons-extended")

//    // shimmer
//    implementation("com.valentinilk.shimmer:compose-shimmer:1.3.0")

    // compress
    implementation("id.zelory:compressor:3.0.1")


    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(kotlin("reflect"))
}
kapt {
    correctErrorTypes = true
}