@file:Pure

package com.predictable.examples

import org.jetbrains.kotlin.compiler.plugin.template.Pure
import java.io.File
import java.util.Random

/**
 * Examples that perform obvious side-effects such as I/O or randomness.
 */
object SideEffectExamples {

    fun readFile(path: String): String =
        File(path).readText() // Performs disk I/O

    fun writeFile(path: String, contents: String) {
        File(path).writeText(contents) // Writes to disk
    }

    fun generateRandomNumber(): Int =
        Random().nextInt() // Non-deterministic
} 
