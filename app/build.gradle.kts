import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
//    id("kotlin-kapt")
    alias(libs.plugins.hiltPlugin)
    id("com.google.gms.google-services")
}

val secretsFile = rootProject.file("secrets.properties")
val secretsProperties = Properties()
if (secretsFile.exists()) {
    secretsProperties.load(FileInputStream(secretsFile))
}

android {
    namespace = "com.example.vchat"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vchat"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("long", "APP_ID", "${secretsProperties.getProperty("APP_ID", "0")}L")
        buildConfigField(
            "String",
            "APP_SIGN",
            "\"${secretsProperties.getProperty("APP_SIGN", "")}\""
        )
        buildConfigField(
            "String",
            "BASE_URL",
            "\"${secretsProperties.getProperty("BASE_URL", "")}\""
        )

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
        compose = true
        buildConfig = true
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
//    My dependencies
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.hilt.android)
//    ksp(libs.hilt.compiler)
//    ksp(libs.hilt.compiler)
//    implementation(libs.hilt.compiler)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson) // For converting JSON responses
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor) // For logging HTTP requests (optional but useful)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.socket.io.client) {
        exclude("org.json", "json")
    }
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.coil.compose)
    // Firebase core
    implementation(libs.firebase.auth.ktx.v2231)

    // Google Play services for authentication
    implementation(libs.play.services.auth)
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:+")
    implementation(libs.permissionx)

//    implementation 'com.github.ZEGOCLOUD:zego_uikit_android:2.13.0'
//    implementation 'com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:2.13.0'


}