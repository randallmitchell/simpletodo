package com.methodsignature.simpletodo.todo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.methodsignature.simpletodo.R

class TodosActivity: AppCompatActivity() {

    private val todos = listOf(
        "Walk the dog",
        "Get an oil change",
        "Mop the kitchen",
        "Do the dishes",
        "Wash the laundry"
    )

    private lateinit var todosView: TodosView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todos_activity)

        todosView = findViewById<DefaultTodosView>(R.id.todos_activity_todos_view) as TodosView
        todosView.displayTodos(todos)
    }
}