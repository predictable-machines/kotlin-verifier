@file:Pure

package com.predictable.examples

import org.jetbrains.kotlin.compiler.plugin.template.Pure

/**
 * Examples that call functions returning `Unit` as an expression, which is a strong
 * indicator of side-effects (e.g. println, logging, etc.).
 */
object UnitExpressionExamples {

    // Prints to standard output, then returns a String
    fun greet(name: String): String {
        println("Hello, $name!")
        return "Greeted $name"
    }

    // Writes to log and ignores the printed output
    fun logAndReturn(value: Int): Int {
        println("logging $value")
        return value
    }

    // Suspend function returning Unit – still performs side-effects
    suspend fun suspendGreet(name: String) {
        println("Hello from suspend: $name")
    }
} 
