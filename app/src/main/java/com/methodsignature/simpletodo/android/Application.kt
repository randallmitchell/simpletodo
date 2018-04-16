package com.methodsignature.simpletodo.android

import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import com.methodsignature.simpletodo.model.SqlBriteDatabase
import com.methodsignature.simpletodo.model.Database
import com.methodsignature.simpletodo.model.DbCallback
import com.squareup.sqlbrite3.SqlBrite
import io.reactivex.schedulers.Schedulers

open class Application: android.app.Application() {

    private val applicationScope: ApplicationScope by lazy {
        ProductionApplicationScope(this)
    }

    open fun getScope(): ApplicationScope {
        return applicationScope
    }
}

interface ApplicationScope {
    fun getDatabase(): Database
}

class ProductionApplicationScope(private val application: Application): ApplicationScope {
    private val db: Database by lazy {
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(application)
            .name("todo.db")
            .callback(DbCallback())
            .build()
        val openHelper = FrameworkSQLiteOpenHelperFactory()
            .create(configuration)
        val sqlBriteDatabase =
            SqlBrite.Builder()
                .build()
                .wrapDatabaseHelper(openHelper, Schedulers.io())
        SqlBriteDatabase(sqlBriteDatabase)
    }

    override fun getDatabase(): Database {
        return db
    }
}