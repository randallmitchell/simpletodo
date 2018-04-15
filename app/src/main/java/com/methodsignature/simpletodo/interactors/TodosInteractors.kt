package com.methodsignature.simpletodo.interactors

import android.util.Log
import com.methodsignature.simpletodo.model.Database
import com.methodsignature.simpletodo.model.Todo
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class InteractorException(message: String, cause: Throwable): Exception(message, cause)

class GetAllTodosInteractor(private val database: Database) {
    /**
     * Fetches all [Todo]s.
     */
    fun get(): Observable<List<Todo>> {
        return database.getAll()
            .onErrorResumeNext { it: Throwable ->
                val message = "Failed to fetch todos"
                Log.e("GetAllTodosInteractor", message, it)
                Observable.error(InteractorException(message, it))
            }
    }
}

class CreateTodoInteractor(private val database: Database) {
    /**
     * Creates a new [Todo] with the given [description] and returns its ID.
     */
    fun create(description: String): Single<Long> {
        return database.create(description)
            .onErrorResumeNext {
                val message = "Failed to create todo"
                Log.e("CreateTodoInteractor", message, it)
                Single.error(InteractorException(message, it))
            }
    }
}

class DeleteTodoInteractor(private val database: Database) {
    /**
     * Deletes a [Todo].
     */
    fun delete(id: Long): Completable {
        return database.delete(id)
            .onErrorResumeNext {
                val message = "Failed to delete todo."
                Log.e("DeleteTodoInteractor", message, it)
                Completable.error(InteractorException(message, it))
            }
    }
}
