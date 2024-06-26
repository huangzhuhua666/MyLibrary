package com.example.hzh.network.bean

import java.io.Serializable

/**
 * Create by hzh on 2020/6/3.
 */
class JsonMap : HashMap<String, Any>(), Serializable {

    companion object {

        private const val serialVersionUID = 1L
    }

    fun getBool(key: String): kotlin.Boolean {
        val obj = get(key)
        return if (obj != null && obj != "") {
            obj.toString().toBoolean()
        } else {
            false
        }
    }

    fun getInt(key: String): Int {
        val obj = get(key)
        return if (obj != null && obj != "") {
            obj.toString().toInt()
        } else {
            0
        }
    }

    fun getString(key: String): String = get(key)?.toString() ?: ""
}

fun jsonMapOf(vararg pairs: Pair<String, Any>): JsonMap = JsonMap().apply { putAll(pairs) }

fun JsonMap.putAll(pairs: Array<out Pair<String, Any>>) {
    for ((key, value) in pairs) {
        put(key, value)
    }
}