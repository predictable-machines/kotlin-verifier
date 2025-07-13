@file:Pure

// RUN_PIPELINE_TILL: FRONTEND

package org.jetbrains.kotlin.compiler.plugin.template

fun printSomething(): Unit {}

fun testPureFile() {
    // These should trigger errors because @Pure is present
    <!MUTABLE_VAR_IN_FUNCTION!>var counter = 0<!>
    <!VAR_ASSIGNMENT_IN_FUNCTION!>counter = 1<!>
    <!UNIT_EXPRESSION_IN_FUNCTION!>printSomething()<!>
} 