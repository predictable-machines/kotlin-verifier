package org.jetbrains.kotlin.compiler.plugin.template

/**
 * Marks a file for purity checking.
 * When this annotation is present at the file level, the Kotlin Verifier plugin
 * will enforce functional programming rules such as:
 * - No mutable variables (var declarations)
 * - No variable assignments
 * - No Unit-returning function calls (side effects)
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
public annotation class Pure 