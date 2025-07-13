@file:Pure

// RUN_PIPELINE_TILL: FRONTEND

package org.jetbrains.kotlin.compiler.plugin.template

fun test() {
    <!MUTABLE_VAR_IN_FUNCTION!>var x = 1<!>
    <!VAR_ASSIGNMENT_IN_FUNCTION!>x = 2<!>
}