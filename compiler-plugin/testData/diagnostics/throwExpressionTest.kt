@file:Pure

// RUN_PIPELINE_TILL: FRONTEND

package org.jetbrains.kotlin.compiler.plugin.template

fun validateInput(value: String): String {
    if (value.isEmpty()) {
        // This should trigger an error - throw expressions are not allowed in pure functions
        <!THROW_EXPRESSION_IN_FUNCTION!>throw IllegalArgumentException("Value cannot be empty")<!>
    }
    return value
}

fun parseOrThrow(str: String): Int {
    // This should also trigger an error - throw in expression position
    return str.toIntOrNull() ?: <!THROW_EXPRESSION_IN_FUNCTION!>throw NumberFormatException("Invalid number")<!>
}

fun handleCase(input: Int): String {
    return when (input) {
        1 -> "One"
        2 -> "Two" 
        // This should trigger an error - throw in when expression
        else -> <!THROW_EXPRESSION_IN_FUNCTION!>throw UnsupportedOperationException("Unsupported input")<!>
    }
} 