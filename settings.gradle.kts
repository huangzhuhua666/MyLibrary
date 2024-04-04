@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
        maven { url = uri("./custom-gradle-plugin-repo") }
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
}

rootProject.name = "MyLibrary"

include(":app")
include(":library-common")
include(":library-base")
include(":library-network")
include(":library-ui")
include(":library-gradle-plugin")