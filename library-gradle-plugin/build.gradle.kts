plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)

    id("java-gradle-plugin")
    id("maven-publish")
    id("signing")
}

gradlePlugin {
    plugins {
        create("mavenPublishPlugin") {
            group = "com.example.plugin"
            version = "1.0.3"
            id = "com.hzh.plugin.mavenpublish"
            implementationClass = "com.example.plugin.publish.MavenPublishPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("../custom-gradle-plugin-repo")
        }
    }
}

dependencies {
    implementation(libs.gradle)
}