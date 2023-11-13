import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties


plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
   id("com.google.devtools.ksp")
}

android {
    namespace = "com.unchil.searchcamp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.unchil.searchcamp"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "MAPS_API_KEY", getApiKey("MAPS_API_KEY"))
        buildConfigField("String", "OPENWEATHER_KEY", getApiKey("OPENWEATHER_KEY"))
        buildConfigField("String", "WORLD_API_KEY", getApiKey("WORLD_API_KEY"))
        buildConfigField("String", "GOCAMPING_API_KEY", getApiKey("GOCAMPING_API_KEY"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures{
        buildConfig = true
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
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

fun getApiKey(propertyKey: String):String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}

dependencies {

    implementation ("com.google.accompanist:accompanist-permissions:0.31.0-alpha")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-android:1.2.0-alpha10")

    implementation ("androidx.compose.material:material:1.6.0-alpha07")
    implementation ("androidx.compose.material:material-icons-extended:1.6.0-alpha07")
    implementation ("androidx.compose.runtime:runtime:1.6.0-alpha07")

    implementation("androidx.wear.compose:compose-material:1.3.0-alpha07")


    implementation ("androidx.compose.foundation:foundation:1.6.0-alpha07")
    implementation ("androidx.compose.foundation:foundation-layout:1.6.0-alpha07")
    implementation ("androidx.compose.animation:animation:1.6.0-alpha07")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.6.0-alpha07")


    // retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:retrofit-mock:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // map
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.maps.android:android-maps-utils:2.2.3")

    // map compose
    implementation ("com.google.maps.android:maps-compose:2.11.2")

    // Optionally, you can include the Compose utils library for Clustering, etc.
    implementation ("com.google.maps.android:maps-compose-utils:2.11.2")

    // Optionally, you can include the widgets library for ScaleBar, etc.
    implementation ("com.google.maps.android:maps-compose-widgets:2.11.2")


    // room
    implementation ("androidx.room:room-runtime:2.5.2")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.10-1.0.13")
    implementation("androidx.leanback:leanback:1.0.0")
    annotationProcessor ("androidx.room:room-compiler:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")
    implementation ("androidx.room:room-ktx:2.5.2")
    implementation ("androidx.room:room-paging:2.5.2")


    implementation ("androidx.paging:paging-runtime-ktx:3.2.0")
    implementation ("androidx.paging:paging-compose:3.2.0")

    //Navigation
    implementation ("androidx.navigation:navigation-compose:2.6.0-rc01")
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.31.2-alpha")

    //WebView
    implementation ("com.google.accompanist:accompanist-webview:0.29.0-alpha")


    implementation ("io.coil-kt:coil-compose:2.2.2")



    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

