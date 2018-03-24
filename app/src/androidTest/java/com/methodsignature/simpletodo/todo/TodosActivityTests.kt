package com.methodsignature.simpletodo.todo

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodosActivityTests {

    @Suppress("unused")
    @get:Rule
    val activityTestRule = ActivityTestRule(TodosActivity::class.java)

    private val todos = listOf(
        "Walk the dog",
        "Get an oil change",
        "Mop the kitchen",
        "Do the dishes",
        "Wash the laundry"
    )

    @Test
    fun displaysTodoTextsOnScreen() {
        todos.forEach {
            onView(withText(it)).check(matches(isDisplayed()))
        }
    }
}