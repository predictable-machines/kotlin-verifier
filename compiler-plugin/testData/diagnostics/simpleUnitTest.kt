@file:Pure

// RUN_PIPELINE_TILL: FRONTEND

package org.jetbrains.kotlin.compiler.plugin.template

fun printMessage(): Unit {}

fun simpleUnitTest() {
    <!UNIT_EXPRESSION_IN_FUNCTION!>printMessage()<!>
} 