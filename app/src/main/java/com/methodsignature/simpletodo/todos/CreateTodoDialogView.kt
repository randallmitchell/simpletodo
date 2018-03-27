package com.methodsignature.simpletodo.todos

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import com.methodsignature.ktandroidext.text.TextWatcher
import com.methodsignature.ktandroidext.widget.DialogView
import com.methodsignature.simpletodo.R


class CreateTodoDialogView: FrameLayout {

    interface Listener {
        fun onCreateTodo(text: String)
        fun onCancel()
    }

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyleAttr: Int
    ): super(context, attributeSet, defStyleAttr)

    private var listener: Listener? = null

    private val dialogView by lazy {
        requireNotNull(findViewById<DialogView>(R.id.create_todo_inner_dialog_view))
    }

    private val textInput by lazy {
        requireNotNull(findViewById<EditText>(R.id.create_todo_dialog_view_contents_title_input))
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun clear() {
        textInput.text.clear()
    }

    fun attemptToShowKeyboard() {
        if (textInput.requestFocus()) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(textInput, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun dismissKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            textInput.applicationWindowToken,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    init {
        LayoutInflater.from(context).inflate(
            R.layout.create_todo_dialog_view,
            this,
            true
        )
        val configurator = dialogView.getConfigurator()

        configurator.apply {
            addTitle(R.string.create_todo_dialog_title)
            setCustomViewLayout(R.layout.create_todo_dialog_view_contents)
            setPositiveButtonListener {
                requireNotNull(listener).onCreateTodo(textInput.text.toString())
            }
            setCancelListener {
                requireNotNull(listener).onCancel()
            }
        }

        fun setPositiveButtonState() {
            configurator.setPositiveButtonIsEnabled(!textInput.text.isEmpty())
        }

        setPositiveButtonState()
        textInput.addTextChangedListener(
            object: TextWatcher() {
                override fun afterTextChanged(s: Editable) {
                    setPositiveButtonState()
                }
            }
        )
    }
}