@file:Pure

// RUN_PIPELINE_TILL: FRONTEND

package org.jetbrains.kotlin.compiler.plugin.template

import org.jetbrains.kotlin.compiler.plugin.template.Pure

fun loopTest() {
    val numbers = listOf(1, 2, 3)
    <!LOOP_EXPRESSION_IN_FUNCTION!>for (n in numbers) {
        // intentionally empty body
    }<!>
} 