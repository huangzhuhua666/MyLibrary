// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.custom.maven.publish) apply false
}

subprojects {
    configurations.all {
        resolutionStrategy {
            force(libs.kotlin.stdlib)
            force(libs.appcompat)
            force(libs.kotlin.reflect)
            force(libs.kotlinx.coroutines.android)
            force(libs.kotlinx.coroutines.core)
            force(libs.constraintlayout)
            force(libs.material)
            force(libs.glide)
            force("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
            force("androidx.fragment:fragment-ktx:1.6.2")
            force("androidx.savedstate:savedstate:1.2.1")
            force("androidx.collection:collection:1.1.0")
            force("androidx.concurrent:concurrent-futures:1.1.0")
            force("androidx.drawerlayout:drawerlayout:1.1.1")
            force("androidx.recyclerview:recyclerview:1.1.0")
            force("androidx.customview:customview:1.1.0")
            force("androidx.arch.core:core-common:2.2.0")
            force("androidx.arch.core:core-runtime:2.2.0")
            force("androidx.lifecycle:lifecycle-process:2.7.0")
            force("androidx.startup:startup-runtime:1.1.1")
            force("androidx.test:monitor:1.6.1")
            force("androidx.annotation:annotation:1.6.0")
            force("androidx.lifecycle:lifecycle-livedata-core-ktx:2.7.0")
            force("org.jetbrains:annotations:23.0.0")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.22")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:1.9.23")
            force("androidx.lifecycle:lifecycle-livedata-core:2.7.0")
            force("androidx.lifecycle:lifecycle-service:2.7.0")
            force("androidx.annotation:annotation-experimental:1.3.0")
            force("androidx.slidingpanelayout:slidingpanelayout:1.2.0")
            force("com.squareup.okhttp3:okhttp:4.12.0")
            force("com.squareup.okio:okio:3.6.0")
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}