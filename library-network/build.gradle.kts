plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.hzh.network"

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
    resourcePrefix("net_")

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
}

dependencies {
    // region kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    implementation("androidx.core:core-ktx:1.9.0")
    // endregion

    // region rxhttp
    val rxHttpVersion = "3.2.5"
    api("com.github.liujingxing.rxhttp:rxhttp:$rxHttpVersion")
    ksp("com.github.liujingxing.rxhttp:rxhttp-compiler:$rxHttpVersion")
    // endregion

    api("com.squareup.okhttp3:okhttp:4.12.0")

    // region test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // endregion
}

ext {
    set("PUBLISH_ARTIFACT_ID", "networklib")
    set("PUBLISH_VERSION", "2.0.0")
}

//apply("../publish-mavencentral.gradle.kts")