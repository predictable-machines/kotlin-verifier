@file:Pure

package com.predictable.examples

import org.jetbrains.kotlin.compiler.plugin.template.Pure

/**
 * Examples that use throw expressions.
 * Throw expressions violate functional purity because they introduce side effects
 * and break referential transparency by using exceptions for control flow.
 * 
 * NOTE: This file does NOT have @file:Pure annotation so these examples compile.
 * Adding @file:Pure would trigger the verifier diagnostics.
 */
object ThrowExpressionExamples {

    // Simple throw expression
    fun validatePositive(n: Int): Int {
        if (n <= 0) {
            throw IllegalArgumentException("Number must be positive")
        }
        return n
    }

    // Throw in expression position (using ?: operator)
    fun parseIntOrThrow(str: String): Int {
        return str.toIntOrNull() ?: throw NumberFormatException("Invalid number: $str")
    }

    // Throw in when expression
    fun handleError(errorCode: Int): String {
        return when (errorCode) {
            404 -> "Not Found"
            500 -> "Internal Server Error"
            else -> throw UnsupportedOperationException("Unknown error code: $errorCode")
        }
    }

    // Function that always throws
    fun unimplementedFeature(): Nothing {
        throw NotImplementedError("This feature is not yet implemented")
    }

    // Nested throw expressions
    fun complexValidation(value: String?): String {
        val trimmed = value?.trim() ?: throw IllegalArgumentException("Value cannot be null")
        if (trimmed.isEmpty()) {
            throw IllegalArgumentException("Value cannot be empty")
        }
        return trimmed
    }

    // Throw with computed exception message
    fun divide(a: Int, b: Int): Double {
        if (b == 0) {
            throw ArithmeticException("Division by zero: $a / $b")
        }
        return a.toDouble() / b
    }
} 
