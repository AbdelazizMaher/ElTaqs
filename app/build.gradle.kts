import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.1.10"
}

val composeUiVersion by extra("1.2.0")
val androidXVersion by extra("1.0.0")
val androidXTestCoreVersion by extra("1.6.1")
val androidXTestExtKotlinRunnerVersion by extra("1.1.3")
val androidXTestRulesVersion by extra("1.2.0")
val androidXAnnotations by extra("1.3.0")
val appCompatVersion by extra("1.4.0")
val archLifecycleVersion by extra("2.8.7")
val archTestingVersion by extra("2.2.0")
val coroutinesVersion by extra("1.5.2")
val cardVersion by extra("1.0.0")
val dexMakerVersion by extra("2.12.1")
val espressoVersion by extra("3.4.0")
val fragmentKtxVersion by extra("1.4.0")
val hamcrestVersion by extra("1.3")
val junitVersion by extra("4.13.2")
val materialVersion by extra("1.4.0")
val recyclerViewVersion by extra("1.2.1")
val robolectricVersion by extra("4.5.1")
val rulesVersion by extra("1.0.1")
val swipeRefreshLayoutVersion by extra("1.1.0")
val timberVersion by extra("4.7.1")
val truthVersion by extra("1.1.2")

android {
    namespace = "com.example.eltaqs"
    compileSdk = 35

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use {
            localProperties.load(it)
        }
    }
    val apiKey = localProperties.getProperty("weatherApiKey") ?: ""
    val googleApiKey = localProperties.getProperty("googleMapApiKey") ?: ""

    defaultConfig {
        applicationId = "com.example.eltaqs"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "WEATHER_API_KEY", "\"$apiKey\"")
        buildConfigField("String", "GOOGLE_MAP_API_KEY", "\"$googleApiKey\"")
        manifestPlaceholders["GOOGLE_MAP_API_KEY"] = googleApiKey
    }

    packagingOptions {
        exclude("META-INF/LICENSE-notice.md")
        exclude("META-INF/LICENSE.md")   // (Optional: to exclude the other license file if needed)
        exclude("META-INF/LICENSE.txt")  // (Optional: in case there are any more conflicts)
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("androidx.work:work-runtime-ktx:2.7.1")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")

    implementation("androidx.room:room-ktx:2.6.1")
    implementation ("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    //Scoped API
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")

    //LiveData & Compose
    val compose_version = "1.0.0"
    implementation ("androidx.compose.runtime:runtime-livedata:$compose_version")

    //navigation
    val nav_version = "2.8.8"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    implementation("androidx.compose.ui:ui:$composeUiVersion")
    implementation("androidx.compose.material:material:$composeUiVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeUiVersion")
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha10")

    implementation ("com.airbnb.android:lottie-compose:6.1.0")

    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:maps-compose:6.4.1")
    implementation("com.google.android.libraries.places:places:3.1.0")
    implementation("com.google.maps.android:places-compose:0.1.3")

    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation("com.google.accompanist:accompanist-drawablepainter:0.35.0-alpha")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation ("androidx.compose.material:material:1.5.4")

    testImplementation ("io.mockk:mockk-agent:1.13.17")
    testImplementation ("io.mockk:mockk-android:1.13.17")

    androidTestImplementation ("io.mockk:mockk-android:1.13.17")
    androidTestImplementation ("io.mockk:mockk-agent:1.13.17")

    //kotlinx-coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    // AndroidX and Robolectric
    testImplementation ("androidx.test.ext:junit-ktx:$androidXTestExtKotlinRunnerVersion")
    testImplementation ("androidx.test:core-ktx:$androidXTestCoreVersion")
    testImplementation ("org.robolectric:robolectric:4.11")
}