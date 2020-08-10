package com.example.mylibrary.app

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.example.hzh.network.NetConfig
import com.example.hzh.network.https.HttpsUtils
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Create by hzh on 2020/07/03.
 */
@GlideModule
class MyAppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(OkHttpClient.Builder().run {
                connectTimeout(NetConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                readTimeout(NetConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(NetConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                HttpsUtils.getSSLSocketFactory()?.run {
                    sslSocketFactory(sslSocketFactory, trustManager)
                }
                build()
            })
        )
    }
}