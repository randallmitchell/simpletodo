package com.methodsignature.simpletodo.model

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import com.methodsignature.simpletodo.TodoModel
import com.squareup.sqlbrite3.BriteDatabase
import com.squareup.sqlbrite3.SqlBrite
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
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

class Database(application: Application) {

    private val db: BriteDatabase

    init {
        val sqlBrite = SqlBrite.Builder().build()

        val configuration = Configuration.builder(application)
            .name("todo.db")
            .callback(DbCallback())
            .build()
        val factory = FrameworkSQLiteOpenHelperFactory()
        val helper = factory.create(configuration)
        db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io())
    }

    fun getAll(): Observable<List<Todo>> {
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

    fun create(description: String): Single<Long> {
        return Single.fromCallable {
            val statement = TodoModel.InsertRow(db.writableDatabase).apply {
                bind(description)
            }
            db.executeInsert(statement.table, statement)
        }
    }

    fun delete(id: Long): Completable {
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