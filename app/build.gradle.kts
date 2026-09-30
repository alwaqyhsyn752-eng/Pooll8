plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.aimassist"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aimassist"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64") }
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17 -frtti -fexceptions"
                arguments += "-DANDROID_STL=c++_shared"
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildFeatures { viewBinding = true; buildConfig = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    packaging {
        jniLibs { useLegacyPackaging = true }
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.coroutines.android)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
}
