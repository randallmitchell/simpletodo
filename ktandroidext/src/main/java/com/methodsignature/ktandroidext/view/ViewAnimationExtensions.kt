package com.methodsignature.ktandroidext.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton

/**
 * Hides a view with a brief fade animation followed by a call to set the view to [View.GONE].
 *
 * If the view is already not [View.VISIBLE], the view will not be updated and the listener,
 * if not null, will be invoked.
 */
fun View.fadeToGone(duration: Long = 300, onCompleteListener: (() -> Unit)? = null) {
    if (visibility != View.VISIBLE) {
        onCompleteListener?.invoke()
    } else {
        animate().apply {
            alpha(0f)
            this.duration = duration
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
fun View.fadeToVisible(duration: Long = 300, onCompleteListener: (() -> Unit)? = null) {
    if (visibility == View.VISIBLE) {
        onCompleteListener?.invoke()
    } else {
        alpha = 0f
        visibility = View.VISIBLE
        animate().apply {
            alpha(1f)
            this.duration = duration
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

/**
 * Animates the current color of a CheckBox text to the [toColor], followed by an invocation of
 * the listener, if the listener is not null.
 *
 * If the [CheckBox.getCurrentTextColor] matches the [toColor], the view will not be updated and
 * the listener, if not null, will be invoked.
 */
fun CompoundButton.animateTextColorChange(duration: Long = 300, toColor: Int, onCompleteListener: (() -> Unit)? = null) {
    if (currentTextColor == toColor) {
        onCompleteListener?.invoke()
    } else {
        val animator = ObjectAnimator.ofInt(
            this,
            "textColor",
            currentTextColor,
            toColor
        )
        animator.apply {
            this.duration = duration
            addListener(
                object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animator: Animator?) {
                        setTextColor(toColor)
                        onCompleteListener?.invoke()
                    }
                }
            )
            setEvaluator(ArgbEvaluator())
            start()
        }
    }
}

fun CompoundButton.animateStrikeThrough(onCompleteListener: (() -> Unit)? = null) {
    val span = SpannableString(text)
    val strikeSpan = StrikethroughSpan()
    val animator = ValueAnimator.ofInt(text.length)
    animator.apply {
        addUpdateListener {
            span.setSpan(
                strikeSpan,
                0,
                it.animatedValue as Int,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text = span
            invalidate()
        }
        addListener(
            object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    onCompleteListener?.invoke()
                }
            }
        )
        start()
    }
}

    fun CompoundButton.addStrikeThrough() {
        val span = SpannableString(text)
        span.setSpan(
            StrikethroughSpan(),
            0,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text = span
    }



fun CompoundButton.animateRemoveStrikeThrough(onCompleteListener: (() -> Unit)? = null) {
    val span = SpannableString(text)
    val strikeSpan = StrikethroughSpan()
    val animator = ValueAnimator.ofInt(text.length, 0)
    animator.apply {
        addUpdateListener {
            span.setSpan(
                strikeSpan,
                0,
                it.animatedValue as Int,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text = span
            invalidate()
        }
        addListener(
            object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    onCompleteListener?.invoke()
                }
            }
        )
        start()
    }
}