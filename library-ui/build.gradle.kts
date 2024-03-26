plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.hzh.ui"

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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    implementation("androidx.core:core-ktx:1.9.0")
    // endregion

    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.google.android.material:material:1.8.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // region test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // endregion
}

ext {
    set("PUBLISH_ARTIFACT_ID", "uilib")
    set("PUBLISH_VERSION", "2.0.0")
}

//apply("../publish-mavencentral.gradle.kts")