@file:Pure

// RUN_PIPELINE_TILL: FRONTEND

package org.jetbrains.kotlin.compiler.plugin.template

// Top-level mutable variable used in the following functions
<!TOP_LEVEL_MUTABLE_VAR!>var globalCounter: Int = 0<!>

object GlobalStateExample {

    fun incrementGlobal(): Int {
        <!VAR_ASSIGNMENT_IN_FUNCTION!>globalCounter += 1<!>
        return globalCounter
    }

    fun readAndResetGlobal(): Int {
        val value = globalCounter
        <!VAR_ASSIGNMENT_IN_FUNCTION!>globalCounter = 0<!>
        return value
    }
} 