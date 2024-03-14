package com.example.hzh.base.util

import android.util.Log
import java.lang.reflect.Array
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Create by hzh on 2024/3/13.
 */
@Suppress("UNCHECKED_CAST")
object ReflectUtils {

    private const val TAG = "ReflectUtils"

    private val sClassCache by lazy { mutableMapOf<String, Class<*>>() }
    private val sDeclaredMethodCache by lazy { mutableMapOf<String, Method>() }
    private val sMethodCache by lazy { mutableMapOf<String, Method>() }
    private val sFieldCache by lazy { mutableMapOf<String, Field>() }

    fun getClass(name: String?): Class<*>? {
        name ?: return null
        if (name.trim().isEmpty()) {
            return null
        }

        synchronized(sClassCache) {
            if (sClassCache.containsKey(name)) {
                return sClassCache[name]
            }
        }

        var clazz: Class<*>? = null
        try {
            clazz = Class.forName(name)?.also {
                synchronized(sClassCache) {
                    sClassCache[name] = it
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "errorMessage = ${e.message} Throwable = $e")
        }
        return clazz
    }

    fun getDeclaredMethod(clazz: Class<*>?, name: String, vararg paramTypes: Class<*>?): Method? {
        clazz ?: return null

        val key = generateMethodKey(clazz, name, *paramTypes)
        synchronized(sDeclaredMethodCache) {
            if (sDeclaredMethodCache.containsKey(key)) {
                return sDeclaredMethodCache.get(key)
            }
        }

        var method: Method? = null
        try {
            method = clazz.getDeclaredMethod(name, *paramTypes).also {
                it.isAccessible = true

                synchronized(sDeclaredMethodCache) {
                    sDeclaredMethodCache[key] = it
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "method $name not found for class ${clazz.name}")
        }
        return method
    }

    private fun generateMethodKey(clazz: Class<*>, name: String, vararg paramTypes: Class<*>?): String {
        var key = "${clazz.name}-$name"
        paramTypes.asSequence()
            .filterNotNull()
            .forEach {
                key += "-${it.name}"
            }
        return key
    }

    fun getMethod(clazz: Class<*>, name: String, vararg paramTypes: Class<*>?): Method? {
        val key = generateMethodKey(clazz, name, *paramTypes)
        synchronized(sMethodCache) {
            if (sMethodCache.containsKey(key)) {
                return sMethodCache[key]
            }
        }

        var method = getDeclaredMethod(clazz, name, *paramTypes)
        if (method == null) {
            for (inter in clazz.interfaces) {
                method = getMethod(inter, name, *paramTypes)
                if (method != null) {
                    break
                }
            }

            val superClazz = clazz.superclass
            if (method == null && superClazz != null) {
                method = getMethod(superClazz, name, *paramTypes)
            }
        }

        method?.let {
            it.isAccessible = true

            synchronized(sDeclaredMethodCache) {
                sDeclaredMethodCache[generateMethodKey(clazz, name, *paramTypes)] = it
            }
        }

        return method
    }

    fun <T> invokeObject(method: Method?, receiver: Any?, vararg params: Any?): T? {
        method ?: return null

        try {
            if (!method.isAccessible) {
                method.isAccessible = true
            }
            return method.invoke(receiver, *params) as T
        } catch (e: Throwable) {
            Log.e(TAG, "errorMessage = ${e.message} Throwable = $e")
        }
        return null
    }

    fun <T> getFieldValue(clazz: Class<*>, receiver: Any?, name: String): T? {
        val field = getField(clazz, name) ?: return null
        try {
            return field[receiver] as T
        } catch (e: Throwable) {
            Log.e(TAG, "getFieldValue ${clazz.name}.$name $e")
        }
        return null
    }

    fun getField(clazz: Class<*>?, name: String): Field? {
        clazz ?: return null

        val key = "${clazz.name}-$name"
        synchronized(sFieldCache) {
            if (sFieldCache.containsKey(key)) {
                return sFieldCache[key]
            }
        }

        var field: Field? = null
        try {
            field = clazz.getDeclaredField(name).also {
                it.isAccessible = true

                synchronized(sFieldCache) {
                    sFieldCache[key] = it
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "field $name not found for class ${clazz.name}")
        }
        return field
    }

    fun setFieldValue(clazz: Class<*>, receiver: Any?, name: String, value: Any?) {
        val field = getField(clazz, name) ?: return
        try {
            field[receiver] = value
        } catch (e: Throwable) {
            Log.e(TAG, "setFieldValue ${clazz.name}.$name $e")
        }
    }

    fun setFieldValue(receiver: Any?, field: Field?, value: Any?) {
        receiver ?: return
        field ?: return

        val isAccessible = field.isAccessible
        try {
            if (!isAccessible) {
                field.isAccessible = true
            }
            field[receiver] = value
        } catch (e: Throwable) {
            Log.e(TAG, "setFieldValue ${receiver.javaClass.name}.${field.name} $e")
        }
    }

    fun getTypeEmptyValue(retType: Class<*>?): Any? {
        retType ?: return null
        if (retType == Void.TYPE) {
            return null
        }

        return when(retType) {
            Int::class.java, Int::class.javaPrimitiveType -> {
                0
            }
            Boolean::class.java, Boolean::class.javaPrimitiveType -> {
                false
            }
            Byte::class.java, Byte::class.javaPrimitiveType -> {
                0.toByte()
            }
            Char::class.java, Char::class.javaPrimitiveType -> {
                Char.MIN_VALUE
            }
            Double::class.java, Double::class.javaPrimitiveType -> {
                0.0
            }
            Float::class.java, Float::class.javaPrimitiveType -> {
                0f
            }
            Long::class.java, Long::class.javaPrimitiveType -> {
                0L
            }
            Short::class.java, Short::class.javaPrimitiveType -> {
                0.toShort()
            }
            MutableList::class.java -> {
                ArrayList<Any>()
            }
            MutableMap::class.java -> {
                HashMap<Any, Any>()
            }
            MutableSet::class.java -> {
                HashSet<Any>()
            }
            else -> {
                if (retType.isArray) {
                    Array.newInstance(retType.componentType, 0)
                } else if (MutableList::class.java.isAssignableFrom(retType)
                    || MutableMap::class.java.isAssignableFrom(retType)
                    || MutableSet::class.java.isAssignableFrom(retType)) {
                    newDefaultConstructorInstance(retType)
                } else {
                    null
                }
            }
        }
    }

    private fun newDefaultConstructorInstance(retType: Class<*>): Any? {
        try {
            return retType.getConstructor()?.newInstance()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return null
    }

    fun getConstructor(clazzName: String?, vararg paramTypes: Class<*>?): Constructor<*>? {
        val clazz = getClass(clazzName) ?: return null
        return try {
            clazz.getConstructor(*paramTypes)?.also {
                it.isAccessible = true
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            null
        }
    }

    fun <T> newObject(cons: Constructor<*>, vararg params: Any?): T? {
        return try {
            return cons.let {
                it.isAccessible = true
                it.newInstance(*params) as T
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    fun isEqualsMethod(method: Method): Boolean {
        val modifier = method.modifiers
        if (Modifier.isStatic(modifier) || !Modifier.isPublic(modifier)) {
            return false
        }
        if ("equals" != method.name) {
            return false
        }

        val args = method.parameterTypes
        return args.size == 1 && args[0] == Any::class.java
    }

    fun isHashCodeMethod(method: Method): Boolean {
        val modifier = method.modifiers
        if (Modifier.isStatic(modifier) || !Modifier.isPublic(modifier)) {
            return false
        }
        if ("hashCode" != method.name) {
            return false
        }

        return method.parameterTypes.isEmpty()
    }
}