package com.example.plugin.publish

import com.example.plugin.dsl.android
import com.example.plugin.dsl.create
import com.example.plugin.dsl.ext
import com.example.plugin.dsl.implementation
import com.example.plugin.dsl.main
import com.example.plugin.dsl.publishing
import com.example.plugin.dsl.register
import com.example.plugin.dsl.signing
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import java.io.FileInputStream
import java.util.*

class MavenPublishPlugin : Plugin<Project> {

    companion object {

        private const val PUBLISH_ARTIFACT_ID = "PUBLISH_ARTIFACT_ID"
        private const val PUBLISH_VERSION = "PUBLISH_VERSION"

        private const val KEY_GIT_USERNAME = "git.username"

        private const val KEY_DEVELOPER_ID = "developer.id"
        private const val KEY_DEVELOPER_NAME = "developer.name"
        private const val KEY_DEVELOPER_EMAIL = "developer.email"

        private const val KEY_SIGNING_KEY_ID = "signing.keyId"
        private const val KEY_SIGNING_PASSWORD = "signing.password"
        private const val KEY_SIGNING_SECRET_KEY_RING_FILE = "signing.secretKeyRingFile"

        private const val KEY_OSSRH_USERNAME = "ossrhUsername"
        private const val KEY_OSSRH_PASSWORD = "ossrhPassword"

        private const val KEY_PUBLISH_ARTIFACT_ID = "publishArtifactId"
        private const val KEY_PUBLISH_VERSION = "publishVersion"
    }

    override fun apply(target: Project) {
        println("== Maven Publish Plugin ==")

        target.run {
            applyPlugins()

            registerTasks()

            afterEvaluate {
                configPublishing(target, loadLocalConfig())

                configSigning()
            }
        }
    }

    private fun Project.applyPlugins() {
        plugins.apply("maven-publish")
        plugins.apply("signing")
    }

    private fun Project.registerTasks() {
        tasks.register<Jar>("androidSourcesJar") {
            archiveClassifier.convention("sources")
            archiveClassifier.set("sources")

            android {
                sourceSets.main?.java?.srcDirs?.let {
                    from(it)
                }
            }

            exclude("**/R.class")
            exclude("**/BuildConfig.class")
        }
    }

    private fun Project.configPublishing(target: Project, config: Map<String, String>) {
        val gitUsername = config[KEY_GIT_USERNAME]
        publishing {
            it.publications { pub ->
                pub.create<MavenPublication>("release") {
                    // The coordinates of the library, being set from variables that
                    // we'll set up in a moment
                    groupId = "io.github.$gitUsername"
                    artifactId = config[KEY_PUBLISH_ARTIFACT_ID]
                    version = config[KEY_PUBLISH_VERSION]

                    // Two artifacts, the `aar` and the sources
                    artifact("$buildDir/outputs/aar/${target.name}-release.aar")
                    artifact("androidSourcesJar")

                    // Self-explanatory metadata for the most part
                    pom { pom ->
                        pom.name.set(config[KEY_PUBLISH_ARTIFACT_ID])
                        description = "Some base and useful library for Android"
                        // If your project has a dedicated site, use its URL here
                        pom.url.set("https://github.com/${gitUsername}/${rootProject.name}")

                        pom.licenses { spec ->
                            spec.license { license ->
                                // 协议类型，一般默认Apache License2.0的话不用改：
                                license.name.set("The Apache License, Version 2.0")
                                license.url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }

                        pom.developers { spec ->
                            spec.developer { developer ->
                                developer.id.set(config[KEY_DEVELOPER_ID])
                                developer.name.set(config[KEY_DEVELOPER_NAME])
                                developer.email.set(config[KEY_DEVELOPER_EMAIL])
                            }
                        }

                        // Version control info, if you're using GitHub, follow the format as seen here
                        pom.scm { scm ->
                            // Git地址：
                            scm.connection.set("scm:git:github.com/$gitUsername/${rootProject.name}.git")
                            scm.developerConnection.set("scm:git:ssh://github.com/$gitUsername/${rootProject.name}.git")
                            // 分支地址：
                            scm.url.set("https://github.com/$gitUsername/${rootProject.name}/tree/main")
                        }

                        // A slightly hacky fix so that your POM will include any transitive dependencies
                        // that your library builds upon
                        pom.withXml { provider ->
                            val dependenciesNode = provider.asNode().appendNode("dependencies")

                            configurations.implementation.allDependencies.forEach { dependency ->
                                dependenciesNode.appendNode("dependency")
                                    .appendNode("groupId", dependency.group)
                                    .appendNode("artifactId", dependency.name)
                                    .appendNode("version", dependency.version)
                            }
                        }
                    }
                }
            }

            it.repositories { repo ->
                // The repository to publish to, Sonatype/MavenCentral
                repo.maven { maven ->
                    // This is an arbitrary name, you may also use "mavencentral" or
                    // any other name that's descriptive for you
                    maven.name = "mavencentral"

                    // You only need this if you want to publish snapshots, otherwise just set the URL
                    // to the release repo directly
                    val url = if (version.toString().endsWith("SNAPSHOT")) {
                        "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                    } else {
                        "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                    }
                    maven.setUrl(url)

                    // The username and password we've fetched earlier
                    maven.credentials { cre ->
                        cre.username = config[KEY_OSSRH_USERNAME]
                        cre.password = config[KEY_OSSRH_PASSWORD]
                    }
                }
            }
        }
    }

    private fun Project.loadLocalConfig(): Map<String, String> {
        val config = mutableMapOf<String, String>()
        val secretPropsFile = rootProject.file("local.properties")
        if (secretPropsFile.exists()) {
            println("Found secret props file, loading props.")

            val properties = Properties()
            properties.load(FileInputStream(secretPropsFile))
            properties.forEach { name, value ->
                println("Find property: name = $name, value = $value.")
                config[name.toString()] = value.toString()
            }
        } else {
            println("No props file, loading env vars.")
        }

        config[KEY_PUBLISH_ARTIFACT_ID] = ext[PUBLISH_ARTIFACT_ID].toString()
        config[KEY_PUBLISH_VERSION] = ext[PUBLISH_VERSION].toString()

        println("Finally config:")
        config.forEach {
            println("${it.key} => ${it.value}")
        }

        return config
    }

    private fun Project.configSigning() {
        signing {
            it.sign(publishing.publications)
        }
    }
}