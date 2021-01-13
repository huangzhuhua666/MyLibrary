package com.example.mylibrary.ui.activity

import android.os.Bundle
import android.util.Log
import com.example.hzh.base.activity.BaseActivity
import com.example.mylibrary.databinding.ActivityNoRegisterBinding

/**
 * Create by hzh on 2021/01/06.
 */
class NoRegisterActivity : BaseActivity<ActivityNoRegisterBinding>() {

    companion object {

        private const val TAG = "Hzh"
    }

    override fun createViewBinding(): ActivityNoRegisterBinding {
        return ActivityNoRegisterBinding.inflate(layoutInflater)
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initListener() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }
}