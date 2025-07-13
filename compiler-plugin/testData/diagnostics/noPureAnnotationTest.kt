// RUN_PIPELINE_TILL: BACKEND

package org.jetbrains.kotlin.compiler.plugin.template

fun printMessage(): Unit {}

fun testNoPureFile() {
    // These should NOT trigger errors because @Pure is not present
    var counter = 0
    counter = 1
    printMessage()
} 