package com.example.mylibrary.ui.activity

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.common.activity.YYActivity
import com.example.hzh.base.util.targetVersion
import com.example.hzh.base.util.vbInflate
import com.example.hzh.ui.utils.filterFastClickListener
import com.example.mylibrary.databinding.ActivityGoodsDetailBinding
import com.example.mylibrary.model.GoodsDetailModel
import com.example.mylibrary.viewmodel.GoodsDetailVM
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

/**
 * Create by hzh on 2020/10/13.
 */
@Suppress("UNCHECKED_CAST")
class GoodsDetailActivity : YYActivity<ActivityGoodsDetailBinding, GoodsDetailVM>() {

    override val mBinding by vbInflate<ActivityGoodsDetailBinding>()

    override val mTitleView: View
        get() = mBinding.rlTitle

    override val mViewModel by viewModels<GoodsDetailVM> {
        object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>) = GoodsDetailVM(GoodsDetailModel()) as T
        }
    }

    override fun initView() {
        Request.Builder().url("").build()
    }

    private fun saveImage(bitmap: Bitmap) {
        val fileName = "hfx_share${System.currentTimeMillis()}.jpg"
        targetVersion(
            Build.VERSION_CODES.Q,
            after = {
                val uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                contentResolver.insert(uri, ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                })?.let {
                    contentResolver.openFileDescriptor(it, "w", null)?.run {
                        val fos = FileOutputStream(fileDescriptor)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        fos.flush()
                        fos.close()
                    }
                }
            },
            before = {
                val pic = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
                val fos = FileOutputStream(pic)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()

                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    pic.absolutePath,
                    fileName,
                    null
                )

                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE))
            }
        )
    }

    override fun initData() {

    }

    override fun initListener() {
        mBinding.run {
            btnBack.filterFastClickListener { finish() }
        }
    }

    override fun bindViewModelObserve() {
        super.bindViewModelObserve()
    }
}