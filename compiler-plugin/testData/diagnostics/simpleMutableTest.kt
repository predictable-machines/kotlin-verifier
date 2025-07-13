@file:Pure

// RUN_PIPELINE_TILL: FRONTEND

package org.jetbrains.kotlin.compiler.plugin.template

fun simpleTest() {
    <!MUTABLE_VAR_IN_FUNCTION!>var x = 1<!>
}