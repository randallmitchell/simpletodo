package com.methodsignature.simpletodo.model

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.db.SupportSQLiteOpenHelper
import com.methodsignature.simpletodo.TodoModel
import com.squareup.sqlbrite3.BriteDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.IOException

data class Todo(val id: Long, val description: String): TodoModel {
    @Suppress("FunctionName")
    override fun _id(): Long {
        return id
    }

    override fun description(): String {
        return description
    }

    companion object {
        val FACTORY = TodoModel.Factory<Todo>(
            TodoModel.Creator<Todo> { _id, description -> Todo(_id, description) }
        )
        val SELECT_ALL_MAPPER = FACTORY.selectAllMapper()
    }
}

interface Database {
    fun getAll(): Observable<List<Todo>>
    fun create(description: String): Single<Long>
    fun delete(id: Long): Completable
}

class SqlBriteDatabase(private val db: BriteDatabase): Database {

    override fun getAll(): Observable<List<Todo>> {
        val query = Todo.FACTORY.selectAll()
        return db.createQuery(query.tables, query.sql)
            .map {
                val cursor = it.run()
                if (cursor == null) {
                    throw IOException("Unable to query database")
                } else {
                    val todos = mutableListOf<Todo>()
                    while (cursor.moveToNext()) {
                        todos.add(Todo.SELECT_ALL_MAPPER.map(cursor))
                    }
                    cursor.close()
                    todos
                }
            }
    }

    override fun create(description: String): Single<Long> {
        return Single.fromCallable {
            val statement = TodoModel.InsertRow(db.writableDatabase).apply {
                bind(description)
            }
            db.executeInsert(statement.table, statement)
        }
    }

    override fun delete(id: Long): Completable {
        return Completable.fromCallable {
            val statement = TodoModel.DeleteRow(db.writableDatabase).apply {
                bind(id)
            }
            db.executeUpdateDelete(statement.table, statement)
        }
    }
}

class DbCallback: SupportSQLiteOpenHelper.Callback(1) {
    override fun onCreate(db: SupportSQLiteDatabase) {
        db.execSQL(TodoModel.CREATE_TABLE)
    }

    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // still on initial version of DB.
    }
}