package com.methodsignature.ktandroidext.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

/**
 * Hides a view with a brief fade animation followed by a call to set the view to [View.GONE].
 *
 * If the view is already not [View.VISIBLE], the view will not be updated and the listener,
 * if not null, will be invoked.
 */
fun View.fadeToGone(onCompleteListener: (() -> Unit)? = null) {
    if (visibility != View.VISIBLE) {
        onCompleteListener?.invoke()
    } else {
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
}

/**
 * Shows a view with a brief fade animation.
 *
 * If the view is already [View.VISIBLE], the view will not be updated and the listener,
 * if not null, will be invoked.
 */
fun View.fadeToVisible(onCompleteListener: (() -> Unit)? = null) {
    if (visibility == View.VISIBLE) {
        onCompleteListener?.invoke()
    } else {
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
}