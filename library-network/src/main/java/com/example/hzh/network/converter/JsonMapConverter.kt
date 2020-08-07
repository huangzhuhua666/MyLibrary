package com.example.hzh.network.converter

import com.example.hzh.network.bean.JsonMap
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.callback.IConverter
import rxhttp.wrapper.utils.GsonUtil
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

/**
 * Create by hzh on 2020/6/3.
 */
@Suppress("UNCHECKED_CAST")
class JsonMapConverter private constructor(private val gson: Gson) : IConverter {

    companion object {

        private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaType()

        fun create(): JsonMapConverter? {
            return create(GsonUtil.buildGson())
        }


        fun create(gson: Gson?): JsonMapConverter? {
            if (gson == null) throw NullPointerException("gson == null")
            return JsonMapConverter(gson)
        }
    }

    override fun <T> convert(body: ResponseBody, type: Type, onResultDecoder: Boolean): T = try {
        val result = body.string().let {
            if (onResultDecoder) RxHttpPlugins.onResultDecoder(it) else it
        }

        when (type) {
            JsonMap::class.java -> convertToJsonMap(result) as T
            String::class.java -> result as T
            else -> gson.fromJson(result, type)
        }
    } catch (e: Exception) {
        throw e
    } finally {
        body.close()
    }

    override fun <T : Any> convert(value: T): RequestBody {
        val typeToken = TypeToken.get(value::class.java) as TypeToken<T>
        val adapter = gson.getAdapter(typeToken) as TypeAdapter<T>
        val buffer = Buffer()

        gson.newJsonWriter(OutputStreamWriter(buffer.outputStream(), StandardCharsets.UTF_8)).let {
            adapter.write(it, value)
            it.close()
        }

        return buffer.readByteString().toRequestBody(MEDIA_TYPE)
    }

    private fun convertToJsonMap(str: String): JsonMap {
        val map = JsonMap()

        if (str.isEmpty()) return map

        JSONObject(str).run {
            val iterator = keys()

            while (iterator.hasNext()) {
                val key = iterator.next()

                handleData(map, this, key)
            }
        }

        return map
    }

    private fun convertToList(array: JSONArray): List<JsonMap> {
        val list = mutableListOf<JsonMap>()

        for (i in 0 until array.length()) {
            val jsonObj = array[i]

            if (jsonObj != null) list.add(convertToJsonMap(jsonObj.toString()))
        }

        return list
    }

    private fun convertToJsonMap(jsonObj: JSONObject): JsonMap {
        val map = JsonMap()

        jsonObj.run {
            val iterator = keys()

            while (iterator.hasNext()) {
                val key = iterator.next()

                handleData(map, jsonObj, key)
            }
        }

        return map
    }

    private fun handleData(map: JsonMap, jsonObj: JSONObject, key: String) {
        jsonObj.run {
            when (val obj = get(key)) {
                is Boolean -> map[key] = getBoolean(key)
                is Int -> map[key] = getInt(key)
                is Long -> map[key] = getLong(key)
                is Double -> map[key] = getDouble(key)
                is String -> map[key] = if (obj.toString() != "null") obj.toString() else ""
                is JSONObject -> {
                    val data = convertToJsonMap(getJSONObject(key))
                    if (data.isNotEmpty()) map[key] = data
                }
                is JSONArray -> {
                    val list = convertToList(getJSONArray(key))
                    if (list.isNotEmpty()) map[key] = list
                }
            }
        }
    }
}