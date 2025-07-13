@file:Pure

package com.predictable.examples

import org.jetbrains.kotlin.compiler.plugin.template.Pure

/**
 * Examples that read or write global state. Pure functions should avoid
 * mutable top-level variables as they introduce hidden dependencies.
 */

// Top-level mutable variable
var globalCounter: Int = 0

object GlobalStateExamples {

    fun incrementGlobal(): Int {
        globalCounter += 1
        return globalCounter
    }

    fun readAndResetGlobal(): Int {
        val value = globalCounter
        globalCounter = 0
        return value
    }
} 
