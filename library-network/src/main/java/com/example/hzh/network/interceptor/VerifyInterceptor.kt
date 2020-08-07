package com.example.hzh.network.interceptor

import android.util.Log
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http.promisesBody
import okio.Buffer
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Create by hzh on 2019/09/06.
 */
private const val TAG = "Http"

private val UTF8 = Charsets.UTF_8

class VerifyInterceptor(val debug: Boolean) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        chain.run {
            if (debug) logRequest(request())

            val start = System.nanoTime()

            val response: Response
            try {
                response = proceed(request())
            } catch (e: Exception) {
                if (debug) Log.d(TAG, "<-- Http Failed: $e")
                throw e
            }

            val end = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)

            return response.logResponse(debug, end)
        }
    }
}

private fun Interceptor.Chain.logRequest(request: Request) {
    val protocol = if (connection() != null) connection()!!.protocol() else Protocol.HTTP_1_1

    request.run {
        Log.d(TAG, "--> $method $url $protocol")

        body?.run {
            if (contentType() != null) Log.d(TAG, "\tContent-Type: ${contentType()}")

            if (contentLength() != -1L) Log.d(TAG, "\tContent-Length: ${contentLength()}")
        }

        headers.names().forEach {
            if (!it.equals("Content-Type", true)
                && !it.equals("Content-Length", true)
            ) Log.d(TAG, "\t$it: ${request.header(it)}")
        }

        Log.d(TAG, " ")

        body?.run {
            if (isReadableText(contentType())) bodyToString()
            else Log.d(TAG, "\tbody: maybe [binary body], omitted!")
        }

        Log.d(TAG, "--> End $method")
    }
}

private fun Response.logResponse(debug: Boolean, costTime: Long): Response {
    newBuilder().build().run {
        try {
            if (debug) Log.d(TAG, "<-- $code $message ${request.url} ${costTime}ms")

            if (debug) headers.names().forEach { Log.d(TAG, "\t$it: ${header(it)}") }

            if (debug) Log.d(TAG, " ")

            if (promisesBody()) {
                if (body == null) return this

                if (isReadableText(body!!.contentType())) {
                    val bytes = body!!.byteStream().readBytes()

                    if (debug) Log.d(
                        TAG, "\tbody: ${String(bytes,
                            UTF8
                        )}")

                    val newBody = bytes.toResponseBody(body!!.contentType())
                    return newBuilder().body(newBody).build()
                } else {
                    if (debug) Log.d(TAG, "\tbody: maybe [binary body], omitted!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (debug) Log.d(TAG, "<-- End Http")
        }
    }
    return this
}

private fun isReadableText(mediaType: MediaType?): Boolean {
    if (mediaType == null) return false

    if (mediaType.type == "text") return true

    return mediaType.subtype.toLowerCase(Locale.US).run {
        contains("x-www-form-urlencoded") || contains("json") || contains("xml") || contains("html")
    }
}

private fun Request.bodyToString() {
    try {
        newBuilder().build().body?.run {
            val buffer = Buffer()
            writeTo(buffer)
            Log.d(
                TAG, "\tbody: ${buffer.readString(
                    UTF8
                )}")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}