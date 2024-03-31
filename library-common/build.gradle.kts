plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.common"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

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

    api(libs.legacy.support.core.ui)

    // region immersionbar
    api(libs.immersionbar)
    api(libs.immersionbar.ktx)
    // endregion

    api(libs.mmkv)

    api(libs.banner)

    api(libs.logger)

    // region SmartRefresh
    api(libs.smart.refresh)
    api(libs.smart.refresh.header)
    // endregion

    api(libs.xpopup)

    api(libs.brvah)

    // region Glide
    api(libs.glide)
    api(libs.glide.integration)
    // endregion

    api(libs.live.data.bus)

    api(libs.multidex)

    api(libs.permissionx)

    api(libs.title)

    api(libs.consecutive.scroller)

    api(libs.tab.layout)

    api(libs.picture.selector)

    api(libs.flexbox)

    // region test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso)
    // endregion
}