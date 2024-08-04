package com.example.plugin.dsl

import com.android.build.gradle.api.AndroidSourceSet
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.TaskContainer

/**
 * Create by hzh on 2024/3/31.
 */
internal inline fun <reified T : Task> TaskContainer.register(
    name: String,
    noinline configuration: T.() -> Unit
) = register(name, T::class.java, configuration)

internal inline fun <reified U : Any> PolymorphicDomainObjectContainer<in U>.create(
    name: String,
    noinline configuration: U.() -> Unit
) = create(name, U::class.java, configuration)

internal inline fun DefaultTask.android(
    configuration: BaseAppModuleExtension.() -> Unit
) = extensions.findByType(BaseAppModuleExtension::class.java)?.let {
    configuration.invoke(it)
}

internal val <T : AndroidSourceSet> NamedDomainObjectContainer<in T>.main: AndroidSourceSet?
    get() = findByName("main") as? AndroidSourceSet

internal val Project.ext: ExtraPropertiesExtension
    get() = extensions.extraProperties

internal val ConfigurationContainer.implementation: Configuration
    get() = getByName("implementation")