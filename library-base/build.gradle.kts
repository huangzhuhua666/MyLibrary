plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.hzh.base"

    compileSdk = 30

    defaultConfig {
        minSdk = 21

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    // 统一资源前缀,规范资源引用
    resourcePrefix("base_")

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
    api("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    api("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    api("androidx.core:core-ktx:1.9.0")
    // endregion

    // region coroutine
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // endregion

    api("androidx.appcompat:appcompat:1.6.1")

    api("com.google.android.material:material:1.8.0")

    api("androidx.lifecycle:lifecycle-extensions:2.2.0")

    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    api("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    api("androidx.constraintlayout:constraintlayout:2.1.4")

    api("androidx.viewpager2:viewpager2:1.0.0")

    // region navigation
    api("androidx.navigation:navigation-fragment-ktx:2.5.3")
    api("androidx.navigation:navigation-ui-ktx:2.5.3")
    // endregion

    // region test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // endregion
}

ext {
    set("PUBLISH_ARTIFACT_ID", "baselib")
    set("PUBLISH_VERSION", "2.0.0")
}

//apply("../publish-mavencentral.gradle.kts")