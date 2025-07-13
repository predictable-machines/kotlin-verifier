package org.jetbrains.kotlin.compiler.plugin.template.fir.checkers

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.compiler.plugin.template.fir.KotlinVerifierErrors
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirLoop
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

/**
 * Checker that reports an error whenever a loop expression (while / do-while / for) is found
 * inside a function that belongs to a file annotated with `@Pure`.
 *
 * The rationale mirrors the other purity-enforcing checkers in this plugin: loops are typically
 * used for mutating state or producing side-effects, which violates the purely-functional
 * expectations we impose on @Pure files. Therefore we forbid their usage entirely.
 */
object LoopExpressionChecker : FirSimpleFunctionChecker(MppCheckerKind.Common) {

    override fun check(declaration: FirSimpleFunction, context: CheckerContext, reporter: DiagnosticReporter) {
        // Only run the check if the file containing [declaration] is annotated with @Pure
        if (PureAnnotationUtils.hasFileLevelPureAnnotation(context)) {
            val functionName = declaration.name.asString()
            val visitor = LoopExpressionVisitor(functionName, context, reporter)
            declaration.body?.accept(visitor)
        }
    }

    private class LoopExpressionVisitor(
        private val functionName: String,
        private val context: CheckerContext,
        private val reporter: DiagnosticReporter
    ) : FirVisitorVoid() {

        override fun visitElement(element: FirElement) {
            // If the current [element] is a loop, report the diagnostic
            if (element is FirLoop /* && element.source?.kind !is KtFakeSourceElementKind */) {
                reporter.reportOn(
                    element.source,
                    KotlinVerifierErrors.LOOP_EXPRESSION_IN_FUNCTION,
                    "Loop expression violates functional purity - loops are not allowed",
                    "in function '$functionName'",
                    context
                )
            }
            // Continue traversing children so we catch nested loops
            element.acceptChildren(this)
        }
    }
} 