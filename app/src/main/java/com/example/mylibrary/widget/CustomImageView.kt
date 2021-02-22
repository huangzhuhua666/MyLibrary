package com.example.mylibrary.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Environment
import android.util.AttributeSet
import android.view.View
import com.example.hzh.base.util.isRealEmpty
import com.example.hzh.base.util.isRealNotEmpty
import com.example.hzh.base.util.yes
import java.io.File

/**
 * Create by hzh on 2/20/21.
 */
class CustomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    private var mBitmap: Bitmap? = null

    init {
        val path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path ?: ""

        path.isRealNotEmpty().yes {
            mBitmap = BitmapFactory.decodeFile("${path}${File.separator}IMG2.jpg")
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mBitmap?.let { canvas?.drawBitmap(it, 0f, 0f, mPaint) }
    }
}