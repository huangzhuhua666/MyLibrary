package com.example.hzh.ui.utils

import android.content.Context
import android.widget.ImageView

/**
 * Create by hzh on 2020/07/28.
 */
interface ImageLoadEngine {

    fun loadImage(context: Context, path: String, imageView: ImageView)
}