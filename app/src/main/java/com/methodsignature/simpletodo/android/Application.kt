package com.methodsignature.simpletodo.android

import com.methodsignature.simpletodo.model.Database

class Application: android.app.Application() {

    val applicationScope: ApplicationScope by lazy {
        ApplicationScope(this)
    }
}

class ApplicationScope(private val application: Application) {
    val database: Database by lazy {
        Database(application)
    }
}