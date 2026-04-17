import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

android {
    namespace = "com.skewnexus.trackhub"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.skewnexus.trackhub"
        minSdk = 26
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    implementation(project(":network:core-network"))
    implementation(project(":network:feat-network"))
    implementation(project(":navigation:core-navigation"))
    implementation(project(":navigation:feat-navigation"))
    implementation(project(":local:core-local"))
    implementation(project(":local:feat-local"))
    implementation(project(":core-ui"))

    implementation(project(":auth:feat-auth"))
    implementation(project(":hub:feat-hub"))
    implementation(project(":hub:core-hub"))
    implementation(project(":notifications:feat-notifications"))
    implementation(project(":notifications:core-notifications"))
    implementation(project(":menu:feat-menu"))
    implementation(project(":menu:core-menu"))

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.compose.main)
    implementation(libs.bundles.compose.navigation)
    implementation(libs.bundles.dependency.injection)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.splashscreen)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.bundles.compose.debug)
}