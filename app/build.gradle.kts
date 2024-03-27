plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")

    id("io.objectbox")
}

tasks.register<Copy>("copyCommitMsgFileToHookDir") {
    from("../jenkins_build")
    into("../.git/hooks")
    include("commit-msg")
}

tasks.register<Exec>("chmodCommitMsgFilePermisstion") {
    commandLine("chmod", "777", "../.git/hooks/commit-msg")
}

android {
    val pkName = "com.example.mylibrary"
    namespace = pkName

    compileSdk = appConfig.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = pkName
        minSdk = appConfig.versions.minSdk.get().toInt()
        targetSdk = appConfig.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

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
    implementation(files(fileTree("libs").include("*.jar")))

    implementation(project(":library-common"))

    ksp(thirdLib.glide.compiler)

    // region test
    testImplementation(androidxLib.junit)
    androidTestImplementation(androidxLib.ext.junit)
    androidTestImplementation(androidxLib.espresso.core)
    // endregion

    debugImplementation("com.squareup.leakcanary:leakcanary-android:3.0-alpha-1")
}