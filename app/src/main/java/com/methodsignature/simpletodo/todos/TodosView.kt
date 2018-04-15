package com.methodsignature.simpletodo.todos

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.Toast
import com.methodsignature.ktandroidext.view.animateStrikeThrough
import com.methodsignature.ktandroidext.view.animateTextColorChange
import com.methodsignature.ktandroidext.view.fadeToGone
import com.methodsignature.ktandroidext.view.fadeToVisible
import com.methodsignature.ktandroidext.view.lazyFindViewById
import com.methodsignature.simpletodo.R

data class TodoViewModel(val id: Long, val text: String)

interface TodosView {
    interface Listener {
        fun onNewTodoSubmitted(text: String)
        fun onTodoMarkedComplete(todo: TodoViewModel)
    }

    fun setListener(listener: Listener)
    fun displayTodos(todoViewModels: List<TodoViewModel>)
    fun removeTodo(todoViewModel: TodoViewModel)
    fun appendTodo(todoViewModel: TodoViewModel)
    fun displayPersistentError()
    fun displayTransientError(message: String)
}

class DefaultTodosView: FrameLayout, TodosView {

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(context, attributeSet, defStyleAttr)

    private var listener: TodosView.Listener? = null

    private val todos = mutableListOf<TodoViewModel>()

    private val adapter = ListAdapter()

    private val todosList: RecyclerView by lazyFindViewById(R.id.todos_view_list)
    private val newTodoButton: View by lazyFindViewById(R.id.todos_view_new_todo_button)
    private val createTodoDialog: CreateTodoDialogView by lazyFindViewById(R.id.todos_view_dialog_view)

    private val emptyListMessage: View by lazyFindViewById(R.id.todos_view_empty_list_message)
    private val errorMessage: View by lazyFindViewById(R.id.todos_view_error_message)

    init {
        LayoutInflater.from(context).inflate(R.layout.todos_view, this, true)
        todosList.layoutManager = LinearLayoutManager(context)
        todosList.setHasFixedSize(true)
        todosList.adapter = adapter

        newTodoButton.setOnClickListener {
            createTodoDialog.fadeToVisible {
                createTodoDialog.attemptToShowKeyboard()
            }
        }

        createTodoDialog.setListener(
            object: CreateTodoDialogView.Listener {
                override fun onCreateTodo(text: String) {
                    createTodoDialog.fadeToGone {
                        createTodoDialog.clear()
                        createTodoDialog.dismissKeyboard()
                    }
                    requireNotNull(listener).onNewTodoSubmitted(text)
                }

                override fun onCancel() {
                    createTodoDialog.fadeToGone {
                        createTodoDialog.clear()
                        createTodoDialog.dismissKeyboard()
                    }
                }
            }
        )
    }

    override fun setListener(listener: TodosView.Listener) {
        this.listener = listener
    }

    override fun displayTodos(todoViewModels: List<TodoViewModel>) {
        todos.clear()
        todos.addAll(todoViewModels)

        emptyListMessage.toggleVisibleOnEmptyList()
        errorMessage.fadeToGone()

        adapter.notifyDataSetChanged()
    }

    override fun displayPersistentError() {
        emptyListMessage.fadeToGone()
        errorMessage.fadeToVisible()
        todosList.fadeToGone()
    }

    override fun displayTransientError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun removeTodo(todoViewModel: TodoViewModel) {
        todos.forEachIndexed { index, todo ->
            if (todo.id == todoViewModel.id) {
                todos.removeAt(index)
                emptyListMessage.toggleVisibleOnEmptyList()
                adapter.notifyItemRemoved(index)
                adapter.notifyItemRangeChanged(index, todos.size)
                return
            }
        }
    }

    override fun appendTodo(todoViewModel: TodoViewModel) {
        todos.add(todoViewModel)

        emptyListMessage.toggleVisibleOnEmptyList()
        errorMessage.fadeToGone()

        adapter.notifyItemInserted(todos.size - 1)
    }

    private class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val checkBox = requireNotNull(view.findViewById<CheckBox>(R.id.todos_view_item_text))
    }

    private inner class ListAdapter: RecyclerView.Adapter<ViewHolder>() {

        override fun getItemCount(): Int {
            return todos.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(
                R.layout.todos_view_item,
                parent,
                false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val todo = todos[position]
            holder.checkBox.apply {
                setTextColor(ContextCompat.getColor(context, R.color.black))
                isEnabled = true
                isChecked = false
                text = todo.text
                setOnCheckedChangeListener { button, isChecked ->
                    button.setOnCheckedChangeListener(null)
                    isEnabled = false
                    if (isChecked) {
                        button.animateStrikeThrough {
                            button.animateTextColorChange(
                                toColor = ContextCompat.getColor(context, R.color.lightGray)
                            ) {
                                requireNotNull(listener).onTodoMarkedComplete(todos[holder.adapterPosition])
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Makes view visible when list is empty and gone when list is not empty.
     */
    private fun View.toggleVisibleOnEmptyList() {
        if (todos.isEmpty()) {
            fadeToVisible()
        } else {
            fadeToGone()
        }
    }
}