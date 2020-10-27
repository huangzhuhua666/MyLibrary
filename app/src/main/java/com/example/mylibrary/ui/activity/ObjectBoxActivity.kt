package com.example.mylibrary.ui.activity

import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chad.library.adapter.base.BaseBinderAdapter
import com.example.common.activity.YYActivity
import com.example.hzh.ui.utils.filterFastClickListener
import com.example.mylibrary.adapter.binder.StudentBinder
import com.example.mylibrary.adapter.binder.TeacherBinder
import com.example.mylibrary.databinding.ActivityObjectBoxBinding
import com.example.mylibrary.model.ObjectBoxModel
import com.example.mylibrary.viewmodel.ObjectBoxVM
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Create by hzh on 2020/08/10.
 */
@Suppress("UNCHECKED_CAST")
class ObjectBoxActivity : YYActivity<ActivityObjectBoxBinding, ObjectBoxVM>() {

    override val isStatusBarDarkFont: Boolean
        get() = true

    override val mViewModel by viewModels<ObjectBoxVM> {
        object : ViewModelProvider.Factory {

            override fun <T : ViewModel?> create(modelClass: Class<T>): T = ObjectBoxVM(ObjectBoxModel()) as T
        }
    }

    private val mTeacherBinder by lazy { TeacherBinder() }
    private val mStudentBinder by lazy { StudentBinder() }
    private val mAdapter by lazy {
        BaseBinderAdapter().apply {
            addItemBinder(mTeacherBinder, TeacherBinder.Diff())
            addItemBinder(mStudentBinder, StudentBinder.Diff())
        }
    }

    override fun createViewBinding(): ActivityObjectBoxBinding {
        return ActivityObjectBoxBinding.inflate(layoutInflater)
    }

    override fun initTitleBar() {
        super.initTitleBar()
        immersionBar { fitsSystemWindows(true) }
    }

    override fun initView() {
        mBinding.rvData.adapter = mAdapter
    }

    override fun initData() {
        mViewModel.getTeacherAll()
    }

    override fun initListener() {
        mBinding.run {
            btnInit.filterFastClickListener { mViewModel.initData() }

            btnAdd.filterFastClickListener { }

            btnDeleteAll.filterFastClickListener { }
        }
    }

    override fun bindViewModelObserve() {
        super.bindViewModelObserve()
        mViewModel.teachers.observe(mContext) {
            mAdapter.setDiffNewData(mutableListOf<Any>().apply {
                it.forEach { teacher ->
                    add(teacher)
                    teacher.students.forEach { student -> add(student) }
                }
            })
        }
    }
}