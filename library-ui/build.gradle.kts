plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

//    alias(libs.plugins.custom.maven.publish)
}

android {
    namespace = "com.example.hzh.ui"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    // 统一资源前缀,规范资源引用
    resourcePrefix("ui_")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // region kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.ktx)
    // endregion

    implementation(libs.appcompat)

    implementation(libs.material)

    implementation(libs.constraintlayout)

    implementation(libs.viewpager2)

    // region test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso)
    // endregion
}

configurations.all {
    resolutionStrategy {
        force(libs.androidx.ktx)
        force("androidx.lifecycle:lifecycle-livedata:2.7.0")
        force("androidx.lifecycle:lifecycle-common:2.7.0")
    }
}

ext {
    set("PUBLISH_ARTIFACT_ID", "uilib")
    set("PUBLISH_VERSION", "2.0.0")
}