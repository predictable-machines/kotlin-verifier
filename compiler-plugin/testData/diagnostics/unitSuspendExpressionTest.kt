@file:Pure

// RUN_PIPELINE_TILL: BACKEND

package org.jetbrains.kotlin.compiler.plugin.template

suspend fun suspendSideEffect(): Unit {}

suspend fun testSuspendAllowed() {
    // Calling suspend function returning Unit should be allowed (no diagnostics)
    suspendSideEffect()
} 