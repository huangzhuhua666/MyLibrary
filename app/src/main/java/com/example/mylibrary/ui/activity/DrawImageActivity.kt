package com.example.mylibrary.ui.activity

import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Environment
import android.view.SurfaceHolder
import com.example.common.activity.UIActivity
import com.example.hzh.base.util.isRealEmpty
import com.example.hzh.base.util.vbInflate
import com.example.hzh.base.util.yes
import com.example.mylibrary.databinding.ActivityDrawImageBinding
import com.gyf.immersionbar.ktx.immersionBar
import java.io.File

/**
 * Create by hzh on 2/20/21.
 */
class DrawImageActivity : UIActivity<ActivityDrawImageBinding>() {

    override val mBinding by vbInflate<ActivityDrawImageBinding>()

    override val isStatusBarDarkFont: Boolean = true

    override fun initTitleBar() {
        super.initTitleBar()
        immersionBar { fitsSystemWindows(true) }
    }

    override fun initView() {

    }

    override fun initData() {
        val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path ?: ""
        path.isRealEmpty().yes { return }

        val image = "${path}${File.separator}IMG2.jpg"

        mBinding.run {
            imageView.setImageBitmap(BitmapFactory.decodeFile(image))

            surfaceView.holder.addCallback(object : SurfaceHolder.Callback {

                override fun surfaceCreated(holder: SurfaceHolder) {
                    holder.lockCanvas()?.run {
                        drawBitmap(
                            BitmapFactory.decodeFile(image),
                            0f,
                            0f,
                            Paint(Paint.ANTI_ALIAS_FLAG)
                        )

                        holder.unlockCanvasAndPost(this)
                    }
                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {

                }
            })
        }
    }

    override fun initListener() {

    }
}