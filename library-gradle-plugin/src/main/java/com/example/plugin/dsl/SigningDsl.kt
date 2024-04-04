package com.example.plugin.dsl

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.plugins.signing.SigningExtension

/**
 * Create by hzh on 2024/3/31.
 */

internal val Project.`signing`: SigningExtension
    get() = extensions.getByName("signing") as SigningExtension

internal fun Project.`signing`(
    configure: Action<SigningExtension>
) = extensions.configure("signing", configure)