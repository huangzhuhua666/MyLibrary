package com.example.hzh.base.util

import java.lang.ref.WeakReference

/**
 * Create by hzh on 2024/3/13.
 */
object CollectionUtils {

    /**
     * 弱引用集合中添加一个元素
     */
    fun <T> addWeakReference(collection: MutableCollection<WeakReference<T>>?, obj: T?) {
        collection ?: return
        obj ?: return

        val found = collection.any {
            it.get() == obj
        }

        if (!found) {
            collection.add(WeakReference(obj))
        }
    }

    /**
     * 从弱引用集合中移除一个元素
     */
    fun <T> removeWeakReference(collection: MutableCollection<WeakReference<T>>?, obj: T?) {
        collection ?: return
        obj ?: return

        collection.find {
            it.get() == obj
        }?.let {
            collection.remove(it)
        }
    }
}