plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

//    alias(libs.plugins.custom.maven.publish)
}

android {
    namespace = "com.example.hzh.base"

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
    api(libs.kotlin.stdlib)
    api(libs.kotlin.reflect)
    api(libs.androidx.ktx)
    // endregion

    // region coroutine
    api(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.coroutines.core)
    // endregion

    api(libs.appcompat)

    api(libs.material)

    api(libs.lifecycle.extensions)

    api(libs.lifecycle.viewmodel.ktx)

    api(libs.lifecycle.livedata.ktx)

    api(libs.constraintlayout)

    api(libs.viewpager2)

    // region navigation
    api(libs.navigation.fragment.ktx)
    api(libs.navigation.ui.ktx)
    // endregion

    // region test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso)
    // endregion
}

ext {
    set("PUBLISH_ARTIFACT_ID", "baselib")
    set("PUBLISH_VERSION", "2.0.0")
}