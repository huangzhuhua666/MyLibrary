package com.example.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.example.common.R
import com.example.hzh.base.util.no
import com.example.hzh.base.util.yes
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView

/**
 * Create by hzh on 2020/07/22.
 */
class GlideEngine private constructor() : ImageEngine {

    companion object {

        fun getInstance(): GlideEngine = HOLDER.instance
    }

    private object HOLDER {

        val instance = GlideEngine()
    }

    /**
     * 加载图片
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).into(imageView)
    }

    /**
     * 加载网络图片适配长图方案
     * # 注意：此方法只有加载网络图片才会回调
     *
     * @param callback 网络图片加载回调监听 {link after version 2.5.1 Please use the #OnImageCompleteCallback#}
     */
    override fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView,
        callback: OnImageCompleteCallback?
    ) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : ImageViewTarget<Bitmap>(imageView) {

                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
                    callback?.onShowLoading()
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    callback?.onHideLoading()
                }

                override fun setResource(resource: Bitmap?) {
                    callback?.onHideLoading()
                    if (resource == null) return

                    val eqLongImage = MediaUtils.isLongImg(resource.width, resource.height)

                    longImageView.isGone = !eqLongImage
                    imageView.isGone = eqLongImage

                    eqLongImage.yes {
                        // 加载长图
                        longImageView.isQuickScaleEnabled = true
                        longImageView.isZoomEnabled = true
                        longImageView.isPanEnabled = true
                        longImageView.setDoubleTapZoomDuration(100)
                        longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                        longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                        longImageView.setImage(
                            ImageSource.bitmap(resource),
                            ImageViewState(0f, PointF(0f, 0f), 0)
                        )
                    }.no { imageView.setImageBitmap(resource) } // 普通图片
                }
            })
    }

    /**
     * 加载网络图片适配长图方案
     * # 注意：此方法只有加载网络图片才会回调
     */
    override fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView
    ) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : ImageViewTarget<Bitmap?>(imageView) {

                override fun setResource(resource: Bitmap?) {
                    if (resource == null) return

                    val eqLongImage = MediaUtils.isLongImg(resource.width, resource.height)
                    longImageView.isGone = !eqLongImage
                    imageView.isGone = eqLongImage

                    eqLongImage.yes {
                        // 加载长图
                        longImageView.isQuickScaleEnabled = true
                        longImageView.isZoomEnabled = true
                        longImageView.isPanEnabled = true
                        longImageView.setDoubleTapZoomDuration(100)
                        longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                        longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                        longImageView.setImage(
                            ImageSource.bitmap(resource),
                            ImageViewState(0f, PointF(0f, 0f), 0)
                        )
                    }.no { imageView.setImageBitmap(resource) } // 普通图片
                }
            })
    }

    /**
     * 加载相册目录
     */
    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .centerCrop()
            .sizeMultiplier(0.5f)
            .placeholder(R.drawable.common_image_place_holder)
            .into(object : BitmapImageViewTarget(imageView) {

                override fun setResource(resource: Bitmap?) {
                    val drawable = RoundedBitmapDrawableFactory.create(context.resources, resource)
                    drawable.cornerRadius = 8f
                    imageView.setImageDrawable(drawable)
                }
            })
    }

    /**
     * 加载gif
     */
    override fun loadAsGifImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).asGif().load(url).into(imageView)
    }

    /**
     * 加载图片列表图片
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .placeholder(R.drawable.common_image_place_holder)
            .into(imageView)
    }
}