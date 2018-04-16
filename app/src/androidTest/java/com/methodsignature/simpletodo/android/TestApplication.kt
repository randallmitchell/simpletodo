package com.methodsignature.simpletodo.android

import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner
import com.methodsignature.simpletodo.model.Database
import com.methodsignature.simpletodo.model.DbCallback
import com.methodsignature.simpletodo.model.SqlBriteDatabase
import com.squareup.sqlbrite3.SqlBrite
import io.reactivex.schedulers.Schedulers

class TestApplication: Application() {

    override fun getScope(): ApplicationScope {
        return TestApplicationScope(this)
    }
}

class TestApplicationScope(private val application: TestApplication): ApplicationScope {

    private val db: Database by lazy {
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(application)
            .name(null)
            .callback(DbCallback())
            .build()
        val openHelper = FrameworkSQLiteOpenHelperFactory()
            .create(configuration)
        val sqlBriteDatabase = SqlBrite.Builder()
            .build()
            .wrapDatabaseHelper(openHelper, Schedulers.io())
        SqlBriteDatabase(sqlBriteDatabase)

    }

    override fun getDatabase(): Database {
        return db
    }
}

class TestRunner: AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): android.app.Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}