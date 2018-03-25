package com.methodsignature.simpletodo.todo

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.methodsignature.simpletodo.R


interface TodosView {
    fun displayTodos(todos: List<String>)
}

class DefaultTodosView: FrameLayout, TodosView {

    interface Listener {
        fun onAddTodoButtonClicked()
    }

    private var listener: Listener? = null

    private val todos = mutableListOf<String>()

    private val adapter = ListAdapter()
    private val todosList by lazy { findViewById<RecyclerView>(R.id.todos_view_list) }

    private val newTodoButton by lazy { findViewById<View>(R.id.todos_view_new_todo_button) }

    constructor(context: Context): super(context) {
        init()
        newTodoButton.setOnClickListener {
            val listener = requireNotNull(listener) { "Listener cannot be null." }
            listener.onAddTodoButtonClicked()
        }
    }

    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet) {
        init()
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attributeSet, defStyleAttr) {
        init()
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.todos_view, this, true)
        todosList.layoutManager = LinearLayoutManager(context)
        todosList.setHasFixedSize(true)
        todosList.adapter = adapter
    }

    override fun displayTodos(todos: List<String>) {
        this.todos.clear()
        this.todos.addAll(todos)
        adapter.notifyDataSetChanged()
    }

    private class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView = requireNotNull(view.findViewById<TextView>(R.id.todos_view_item_text))
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
            val text = todos[position]
            holder.textView.text = text
        }
    }
}