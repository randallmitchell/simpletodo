package com.methodsignature.simpletodo.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.methodsignature.simpletodo.todos.TodosActivity
import com.methodsignature.simpletodo.todos.TodosActivityScope

@SuppressLint("Registered")
open class BaseActivity: AppCompatActivity() {

    private val applicationScope by lazy {
        (applicationContext as Application).applicationScope
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this is TodosActivity) {
            TodosActivityScope(applicationScope).bind(this)
        }
    }
}