package com.methodsignature.simpletodo.todos

import com.methodsignature.simpletodo.android.ApplicationScope
import com.methodsignature.simpletodo.interactors.CreateTodoInteractor
import com.methodsignature.simpletodo.interactors.DeleteTodoInteractor
import com.methodsignature.simpletodo.interactors.GetAllTodosInteractor

class TodosActivityScope(private val applicationScope: ApplicationScope) {
    fun bind(activity: TodosActivity) {
        val db = applicationScope.getDatabase()
        activity.getAllTodosInteractor = GetAllTodosInteractor(db)
        activity.createTodoInteractor = CreateTodoInteractor(db)
        activity.deleteTodoInteractor = DeleteTodoInteractor(db)
    }
}