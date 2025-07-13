@file:Pure

package com.predictable.examples

import org.jetbrains.kotlin.compiler.plugin.template.Pure

/**
 * Examples of impure functions that rely on loop constructs.
 * These compile in Kotlin but violate functional purity due to
 * explicit mutation within `for`, `while`, and `do-while` loops.
 */
object LoopExamples {

    // Uses `for` loop to mutate local accumulator
    fun sumUsingForLoop(xs: List<Int>): Int {
        var sum = 0
        for (x in xs) {
            sum += x
        }
        return sum
    }

    // Uses `while` loop with mutable counter
    fun factorialUsingWhile(n: Int): Int {
        var acc = 1
        var i = n
        while (i > 1) {
            acc *= i
            i--
        }
        return acc
    }

    // Uses `do-while` loop with mutable state
    fun countDownFrom(n: Int): List<Int> {
        var i = n
        val result = mutableListOf<Int>()
        do {
            result.add(i)
            i--
        } while (i >= 0)
        return result
    }
} 
