package com.methodsignature.ktandroidext.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

fun View.hideWithFade(onCompleteListener: (() -> Unit)? = null) {
    animate().apply {
        alpha(0f)
        duration = 300
        setListener(
            object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    visibility = View.GONE
                    onCompleteListener?.invoke()
                }
            }
        )
    }
}

fun View.showWithFade(onCompleteListener: (() -> Unit)? = null) {
    alpha = 0f
    visibility = View.VISIBLE
    animate().apply {
        alpha(1f)
        duration = 300
        setListener(
            object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    onCompleteListener?.invoke()
                }
            }
        )
    }
}