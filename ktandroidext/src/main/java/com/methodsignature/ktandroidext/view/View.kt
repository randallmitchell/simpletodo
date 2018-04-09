package com.methodsignature.ktandroidext.view

import android.support.annotation.IdRes
import android.view.View

fun <T: View> View.lazyFindViewById(@IdRes viewId: Int): Lazy<T> {
    return lazy { findViewById<T>(viewId) }
}