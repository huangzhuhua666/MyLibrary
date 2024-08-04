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
            version = "1.0.5"
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

configurations.all {
    resolutionStrategy {
        force("org.ow2.asm:asm:9.2")
        force("org.ow2.asm:asm-commons:9.2")
        force("org.ow2.asm:asm-util:9.2")
        force("org.checkerframework:checker-qual:3.12.0")
        force("com.android.tools.build:aapt2-proto:8.1.4-10154469")
        force("com.google.protobuf:protobuf-java-util:3.19.3")
        force("commons-codec:commons-codec:1.11")
        force("org.apache.httpcomponents:httpclient:4.5.13")
        force("org.apache.httpcomponents:httpcore:4.4.15")
    }
}