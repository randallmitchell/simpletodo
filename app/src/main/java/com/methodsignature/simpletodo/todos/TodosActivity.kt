package com.methodsignature.simpletodo.todos

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.methodsignature.ktandroidext.app.lazyFindViewById
import com.methodsignature.simpletodo.R

class TodosActivity: AppCompatActivity() {

    private val todosView: TodosView by lazyFindViewById<DefaultTodosView>(R.id.todos_activity_todos_view)

    private var incrementedId = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todos_activity)

        todosView.setListener(
            object: TodosView.Listener {
                override fun onCreateTodoButtonClicked() {
                    todosView.showCreateNewTodo()
                }

                override fun onCreateNewTodoCancelRequested() {
                    todosView.closeCreateNewTodo()
                }

                override fun onNewTodo(text: String) {
                    todosView.closeCreateNewTodo()
                    todosView.appendTodo(TodoViewModel(incrementedId++, text))
                }

                override fun onTodoCheckBoxStateChanged(todo: TodoViewModel, isChecked: Boolean) {
                    if (isChecked) {
                        todosView.removeTodo(todo)
                    }
                }
            }
        )
        todosView.displayTodos(listOf())
    }
}