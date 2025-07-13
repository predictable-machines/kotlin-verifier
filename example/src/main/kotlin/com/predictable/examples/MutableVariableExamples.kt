@file:Pure

package com.predictable.examples

import org.jetbrains.kotlin.compiler.plugin.template.Pure

/**
 * Examples that declare or mutate `var` values.
 * Mutable state breaks functional purity even without explicit loops.
 */
object MutableVariableExamples {

    // Declares local mutable variable and mutates it
    fun counter(): Int {
        var c = 0
        c += 1
        c += 1
        return c
    }

    // Declares mutable property in object
    var objectCounter: Int = 0
        private set

    fun incrementObjectCounter(): Int {
        objectCounter++
        return objectCounter
    }
} 
