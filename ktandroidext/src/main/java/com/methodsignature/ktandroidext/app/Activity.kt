package com.methodsignature.ktandroidext.app

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View

fun <T: View> Activity.lazyFindViewById(@IdRes viewId: Int): Lazy<T> {
    return lazy { findViewById<T>(viewId) }
}