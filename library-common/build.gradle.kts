plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.common"

    compileSdk = appConfig.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = appConfig.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    // 统一资源前缀,规范资源引用
    resourcePrefix("common_")

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
    // region my lib
    api(project(":library-base"))
    api(project(":library-network"))
    api(project(":library-ui"))
    // endregion

    api("androidx.legacy:legacy-support-core-ui:1.0.0")

    // region immersionbar
    api(thirdLib.immersionbar)
    api(thirdLib.immersionbar.ktx)
    // endregion

    api(thirdLib.mmkv)

    api(thirdLib.banner)

    api(thirdLib.logger)

    // region SmartRefresh
    api(thirdLib.smartRefresh)
    api(thirdLib.smartRefresh.header)
    // endregion

    api(thirdLib.xpopup)

    api(thirdLib.brvah)

    // region Glide
    api(thirdLib.glide)
    api(thirdLib.glide.integration)
    // endregion

    api(thirdLib.liveDataBus)

    api(thirdLib.multidex)

    api(thirdLib.permissionx)

    api(thirdLib.title)

    api(thirdLib.consecutiveScroller)

    api(thirdLib.tabLayout)

    api(thirdLib.pictureSelector)

    api(androidxLib.flexbox)

    // region test
    testImplementation(androidxLib.junit)
    androidTestImplementation(androidxLib.ext.junit)
    androidTestImplementation(androidxLib.espresso.core)
    // endregion
}