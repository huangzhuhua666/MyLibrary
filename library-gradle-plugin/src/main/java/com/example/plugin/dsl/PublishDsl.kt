package com.example.plugin.dsl

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension

/**
 * Create by hzh on 2024/3/31.
 */

internal val Project.publishing: PublishingExtension
    get() = extensions.getByName("publishing") as PublishingExtension

internal fun Project.publishing(
    configure: Action<PublishingExtension>
): Unit = extensions.configure("publishing", configure)