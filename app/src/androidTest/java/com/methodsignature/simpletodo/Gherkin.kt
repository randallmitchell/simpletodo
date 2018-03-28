package com.methodsignature.simpletodo

fun given(message: String, action: () -> Unit) {
    System.out.println("Given $message")
    action()
}

fun `when`(message: String, action: () -> Unit) {
    System.out.println("When $message")
    action()
}

fun then(message: String, assertion: () -> Unit) {
    System.out.print("Then $message")
    assertion()
}