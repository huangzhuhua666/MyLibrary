@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "io.objectbox" -> {
                    useModule("io.objectbox:objectbox-gradle-plugin:3.8.0")
                }
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }

    versionCatalogs {
        create("appConfig") {
            version("compileSdk", "30")
            version("minSdk", "21")
            version("targetSdk", "30")
        }

        create("androidxLib") {
            // region test
            library("junit", "junit:junit:4.13.2")
            library("ext-junit", "androidx.test.ext:junit:1.1.5")
            library("espresso-core", "androidx.test.espresso:espresso-core:3.5.1")
            // endregion

            library("flexbox", "com.google.android.flexbox:flexbox:3.0.0")
        }

        create("thirdLib") {
            // region glide
            library("glide", "com.github.bumptech.glide:glide:5.0.0-rc01")
            library("glide-integration", "com.github.bumptech.glide:okhttp3-integration:5.0.0-rc01")
            library("glide-compiler", "com.github.bumptech.glide:compiler:5.0.0-rc01")
            // endregion

            // region immersionbar
            library("immersionbar", "com.geyifeng.immersionbar:immersionbar:3.2.2")
            library("immersionbar-ktx", "com.geyifeng.immersionbar:immersionbar-ktx:3.2.2")
            // endregion

            library("mmkv", "com.tencent:mmkv:1.3.4")

            library("multidex", "androidx.multidex:multidex:2.0.1")

            // region SmartRefresh
            library("smartRefresh", "io.github.scwang90:refresh-layout-kernel:2.1.0")
            library("smartRefresh-header", "io.github.scwang90:refresh-header-classics:2.1.0")
            // endregion

            library("brvah", "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.3")

            library("liveDataBus", "io.github.jeremyliao:live-event-bus-x:1.8.0")

            library("logger", "com.orhanobut:logger:2.2.0")

            library("banner", "com.github.zhpanvip:BannerViewPager:3.1.2")

            library("xpopup", "com.github.li-xiaojun:XPopup:2.10.0")

            library("permissionx", "com.guolindev.permissionx:permissionx:1.7.1")

            library("consecutiveScroller", "com.github.donkingliang:ConsecutiveScroller:4.1.0")

            library("title", "com.github.getActivity:TitleBar:10.5")

            library("tabLayout", "com.github.angcyo.DslTablayout:TabLayout:1.5.5")

            library("pictureSelector", "com.github.LuckSiege.PictureSelector:picture_library:v2.5.8")
        }
    }
}

rootProject.name = "MyLibrary"

include(":app")
include(":library-common")
include(":library-base")
include(":library-network")
include(":library-ui")