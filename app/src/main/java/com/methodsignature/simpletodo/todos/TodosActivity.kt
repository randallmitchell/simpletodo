package com.methodsignature.simpletodo.todos

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.methodsignature.simpletodo.R

class TodosActivity: AppCompatActivity() {

    private val todos = mutableListOf<String>()

    private lateinit var todosView: DefaultTodosView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todos_activity)

        todosView = findViewById(R.id.todos_activity_todos_view)

        todosView.setListener(
            object: DefaultTodosView.Listener {
                override fun onCreateTodoButtonClicked() {
                    todosView.showCreateNewTodo()
                }

                override fun onCreateNewTodoCancelRequested() {
                    todosView.closeCreateNewTodo()
                }

                override fun onNewTodo(text: String) {
                    todos.add(text)
                    todosView.closeCreateNewTodo()
                    todosView.displayTodos(todos)
                }
            }
        )
        todosView.displayTodos(todos)
    }
}