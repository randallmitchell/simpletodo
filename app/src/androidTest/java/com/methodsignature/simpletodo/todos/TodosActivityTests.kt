package com.methodsignature.simpletodo.todos

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.methodsignature.simpletodo.R
import com.methodsignature.simpletodo.`when`
import com.methodsignature.simpletodo.given
import com.methodsignature.simpletodo.then
import org.hamcrest.Matchers.not
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
            `when`("a user adds a todo") { aUserAddsATodo(it) }
        }
        todos.forEach {
            then("a todo item should be displayed") { aTodoItemWithTextShouldBeDisplayed(it) }
        }
    }

    @Test
    fun emptyListIsVisibleByDefault() {
        then("the empty list message should be visible") { assertMessageViewVisibility(true) }
    }

    @Test
    fun emptyListIsNotVisibleWhenListIsNotEmpty() {
        `when`("a todo item is added") { aUserAddsATodo(todos[0]) }
        then("the empty list message should not be visible") { assertMessageViewVisibility(false) }
    }

    @Test
    fun aDeletedItemIsRemoved() {
        todos.forEach {
            given("a todo item is added") { aUserAddsATodo(it) }
        }
        val deletedItem = todos[3]
        `when`("todo '$deletedItem' is completed by the user") { aUserMarksTodoAsComplete(deletedItem) }
        then("a todo item should not be displayed") { aTodoItemWithTextShouldNotBeDisplayed(deletedItem) }
    }

    @Test
    fun emptyListMessageAppearsWhenListIsEmptied() {
        val item = todos[0]
        given("a todo item is added") { aUserAddsATodo(item) }
        `when`("todo '$item' is completed by the user") { aUserMarksTodoAsComplete(item) }
        then("the empty list message should be visible") { assertMessageViewVisibility(true) }
    }

    private fun assertMessageViewVisibility(shouldBeVisible: Boolean) {
        val assertion = if (shouldBeVisible) {
            isDisplayed()
        } else {
            not(isDisplayed())
        }
        onView(withId(R.id.todos_view_empty_list_message)).check(matches(assertion))
    }

    private fun aUserAddsATodo(todo: String) {
        onView(withId(R.id.todos_view_new_todo_button)).perform(click())
        onView(withId(R.id.create_todo_dialog_view_contents_title_input)).perform(typeText(todo))
        onView(withId(R.id.ktae_dialog_positive_button)).perform(click())
    }

    private fun aTodoItemWithTextShouldBeDisplayed(todo: String) {
        onView(withText(todo)).check(matches(isDisplayed()))
    }

    private fun aTodoItemWithTextShouldNotBeDisplayed(todo: String) {
        onView(withText(todo)).check(doesNotExist())
    }

    private fun aUserMarksTodoAsComplete(todo: String) {
        onView(withText(todo)).perform(click())
    }
}