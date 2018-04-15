package com.methodsignature.simpletodo.todos

import android.os.Bundle
import com.methodsignature.ktandroidext.app.lazyFindViewById
import com.methodsignature.simpletodo.R
import com.methodsignature.simpletodo.android.BaseActivity
import com.methodsignature.simpletodo.interactors.CreateTodoInteractor
import com.methodsignature.simpletodo.interactors.DeleteTodoInteractor
import com.methodsignature.simpletodo.interactors.GetAllTodosInteractor
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TodosActivity: BaseActivity() {

    private val todosView: TodosView by lazyFindViewById<DefaultTodosView>(R.id.todos_activity_todos_view)

    lateinit var getAllTodosInteractor: GetAllTodosInteractor
    lateinit var createTodoInteractor: CreateTodoInteractor
    lateinit var deleteTodoInteractor: DeleteTodoInteractor

    private var getTodosDisposable: Disposable? = null
    private var createTodoDisposable: Disposable? = null
    private var deleteTodoDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todos_activity)

        todosView.setListener(
            object: TodosView.Listener {
                override fun onNewTodoSubmitted(text: String) {
                    createTodoDisposable = createTodoInteractor.create(text)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                // getAllTodosInteractor will recieve update
                                createTodoDisposable = null
                            },
                            {
                                createTodoDisposable = null
                                todosView.displayTransientError(getString(R.string.create_todo_error))
                            }
                        )
                }

                override fun onTodoMarkedComplete(todo: TodoViewModel) {
                    deleteTodoDisposable = deleteTodoInteractor.delete(todo.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                deleteTodoDisposable = null
                                todosView.removeTodo(todo)
                            },
                            {
                                deleteTodoDisposable = null
                                todosView.displayTransientError(getString(R.string.delete_todo_error))
                            }
                        )
                }
            }
        )

        getTodosDisposable = getAllTodosInteractor.get()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Observable.fromIterable(it)
                    .map { TodoViewModel(it.id, it.description) }
                    .toList()
                    .toObservable()
            }
            .subscribe(
                { todosView.displayTodos(it) },
                { todosView.displayPersistentError() }
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        getTodosDisposable?.dispose()
        getTodosDisposable = null

        createTodoDisposable?.dispose()
        createTodoDisposable = null

        deleteTodoDisposable?.dispose()
        deleteTodoDisposable = null
    }
}
