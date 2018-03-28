package com.methodsignature.ktandroidext.widget

import android.app.AlertDialog
import android.app.DialogFragment
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.methodsignature.ktandroidext.R

/**
 * A view that acts similar to an [AlertDialog] or a [DialogFragment] in respect to UI experience.
 * It's primary purpose is to provide that dialog functionality that is part of the standard view
 * hierarchy where it can be treated as a normal view.
 *
 * One benefit of [DialogView] is that it does not mutate it's own state based on user interaction.
 * This allows support for a more advance view model which can be the sole determining factor in
 * whether the dialog, as part of the view, should be visible.  For example, [DialogView] might
 * be beneficial to MVP, MVVM, or especially MVI architectures.
 *
 * Alternatively, [DialogView] might benefit anyone just looking to have more control over their UI
 * or those wh do not like the way that dialogs are so closely tied to the system, raising issues
 * like having to address responses from dialogs separate and away from code that displays those
 * same dialogs.
 *
 * Since [DialogView] cannot be part of the backstack, back press handling must be handled outside of
 * the [DialogView], presumably by the view controller or presenter receiving a back press
 * notification from the activity or fragment.  This is part of what giving control back to ourselves
 * means - we exchange that control for the free work provided by [DialogFragment].
 */
class DialogView: FrameLayout {

    constructor(context: Context): super(context)

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.ktae_dialog_view, this, true)
        innerViewGroup.setOnClickListener {  }
        readInAttributeSet(attributeSet)
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyleAttr: Int
    ): super(context, attributeSet, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.ktae_dialog_view, this, true)
        innerViewGroup.setOnClickListener {  }
        outerViewGroup.setOnClickListener {  }
        readInAttributeSet(attributeSet, defStyleAttr)
    }

    private fun readInAttributeSet(attributeSet: AttributeSet, defStyleAttr: Int = 0) {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.KtaeDialogView,
            defStyleAttr,
            0
        )

        if (typedArray.hasValue(R.styleable.KtaeDialogView_ktae_dialog_title)) {
            titleText.apply {
                text = typedArray.getText(R.styleable.KtaeDialogView_ktae_dialog_title)
                visibility = VISIBLE
            }
        }
        if (typedArray.hasValue(R.styleable.KtaeDialogView_ktae_dialog_message)) {
            messageText.apply {
                text = typedArray.getText(R.styleable.KtaeDialogView_ktae_dialog_message)
                visibility = VISIBLE
            }
        }

        // don't make buttons visible since they still don't have listeners.
        if (typedArray.hasValue(R.styleable.KtaeDialogView_ktae_dialog_message_positive_button)) {
            positiveButton.text = typedArray.getText(
                R.styleable.KtaeDialogView_ktae_dialog_message_positive_button
            )
        }
        if (typedArray.hasValue(R.styleable.KtaeDialogView_ktae_dialog_message_negative_button)) {
            negativeButton.text = typedArray.getText(
                R.styleable.KtaeDialogView_ktae_dialog_message_negative_button
            )
        }

        typedArray.recycle()
    }

    private val configurator = Configurator(this)

    private val outerViewGroup by lazy { findViewById<ViewGroup>(R.id.dialog_outer_view_group) }
    private val innerViewGroup by lazy { findViewById<ViewGroup>(R.id.ktae_dialog_inner_view_group) }
    private val titleText by lazy { findViewById<TextView>(R.id.ktae_dialog_title) }
    private val messageText by lazy { findViewById<TextView>(R.id.ktae_dialog_message) }
    private val customViewContainer by lazy { findViewById<ViewGroup>(R.id.ktae_dialog_custom_view_group) }
    private val positiveButton by lazy { findViewById<TextView>(R.id.ktae_dialog_positive_button) }
    private val negativeButton by lazy { findViewById<TextView>(R.id.ktae_dialog_negative_button) }

    fun getConfigurator():Configurator {
        return configurator
    }

    /**
     * @param dialog An instance of [DialogView]
     */
    class Configurator internal constructor(private val dialog: DialogView) {

        fun setCustomViewLayout(resId: Int) {
            dialog.customViewContainer.apply {
                visibility = VISIBLE
                LayoutInflater.from(dialog.context).inflate(resId, this, true)
            }
        }

        /**
         * Displays a title with text loaded from given resource ID.
         */
        fun addTitle(resId: Int) {
            dialog.titleText.apply {
                setText(resId)
                visibility = VISIBLE
            }
        }

        /**
         * Displays a message with text loaded from given resource ID.
         */
        fun addMessage(resId: Int) {
            dialog.messageText.apply {
                setText(resId)
                visibility = VISIBLE
            }
        }

        /**
         * Displays a positive button with text loaded from a given resource ID that alerts the
         * give listener when clicked.
         */
        fun addPositiveButton(textStringResId: Int, listener: () -> Unit) {
            dialog.positiveButton.apply {
                setText(textStringResId)
                setOnClickListener { listener() }
                visibility = VISIBLE
            }
        }

        /**
         * Displays a positive button that alerts the given listener when clicked. Displays text value
         * set in layout with 'ktae_dialog_message_positive_button', or default Android text if value
         * is not provided in layout.
         */
        fun setPositiveButtonListener(listener: () -> Unit) {
            dialog.positiveButton.apply {
                setOnClickListener { listener() }
                visibility = View.VISIBLE
            }
        }

        /**
         * Displays a cancel button with text loaded from a given resource ID that alerts the
         * given listener when clicked.
         *
         * The cancel button must first be display by calling [addCancelButton]
         *
         * User [setCancelListener] to listen for cancel requests generated by button and click away
         * functionality.
         */
        fun addCancelButton(stringResId: Int) {
            dialog.negativeButton.apply {
                setText(stringResId)
                visibility = VISIBLE
            }
        }

        /**
         * Displays a cancel button that alerts the listener added by calling [setCancelListener] when
         * clicked. Displays text value set in layout with 'ktae_dialog_message_negative_button', or
         * default Android text if the value is not provided in layout.
         *
         * User [setCancelListener] to listen for cancel requests generated by button and click away
         * functionality.
         */
        fun addCancelButton() {
            dialog.negativeButton.visibility = VISIBLE
        }

        /**
         * Sets a listener that is alerted when a user clicks the cancel button or clicks away from
         * the dialog.
         */
        fun setCancelListener(listener: () -> Unit) {
            dialog.negativeButton.apply {
                setOnClickListener { listener() }
            }
            dialog.outerViewGroup.setOnClickListener { listener() }
        }

        /**
         * Displays the positive button as disabled if the button is added.
         */
        fun setPositiveButtonIsEnabled(isEnabled: Boolean) {
            dialog.positiveButton.isEnabled = isEnabled
        }

        /**
         * Displays the negative button as disabled if the button is added.
         */
        fun setNegativeButtonIsEnabled(isEnabled: Boolean) {
            dialog.negativeButton.isEnabled = isEnabled
        }
    }
}