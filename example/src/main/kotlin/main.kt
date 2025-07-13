@file:Pure

package com.predictable

import org.jetbrains.kotlin.compiler.plugin.template.Pure

/**
 * This module now includes a catalogue of *impure* program snippets located in
 * `com.predictable.examples.*`. Each file demonstrates a different kind of
 * impurity that the standard Kotlin compiler happily accepts but that our
 * Kotlin Verifier plug-in is designed (or planned) to reject when the code is
 * annotated with `@Pure`.
 *
 * 1. `LoopExamples.kt` – mutations inside `for`/`while`/`do-while` loops
 * 2. `MutableVariableExamples.kt` – local and top-level `var` usage
 * 3. `UnitExpressionExamples.kt` – calls to `Unit`-returning functions like `println`
 * 4. `GlobalStateExamples.kt` – reads/writes of global mutable state
 * 5. `SideEffectExamples.kt` – explicit I/O, randomness, etc.
 * 6. `MutableCollectionExamples.kt` – in-place collection updates
 * 7. `ThrowExpressionExamples.kt` – throw expressions that violate functional purity
 *
 * The examples compile successfully because they do **not** place the `@file:Pure`
 * annotation at the top of their files. Adding that annotation would trigger
 * the verifier diagnostics and demonstrate the purity violations.
 */
