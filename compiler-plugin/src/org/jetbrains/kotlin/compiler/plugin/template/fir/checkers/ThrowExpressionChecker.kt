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
import org.jetbrains.kotlin.fir.expressions.FirThrowExpression
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

/**
 * Checker that reports an error whenever a throw expression is found
 * inside a function that belongs to a file annotated with `@Pure`.
 *
 * Throw expressions are side effects that violate functional purity principles
 * since exceptions represent non-local control flow and break referential transparency.
 */
object ThrowExpressionChecker : FirSimpleFunctionChecker(MppCheckerKind.Common) {

    override fun check(declaration: FirSimpleFunction, context: CheckerContext, reporter: DiagnosticReporter) {
        // Only run purity checks if @Pure annotation is present
        if (PureAnnotationUtils.hasFileLevelPureAnnotation(context)) {
            val functionName = declaration.name.asString()
            val visitor = ThrowExpressionVisitor(functionName, context, reporter)
            declaration.body?.accept(visitor)
        }
    }

    private class ThrowExpressionVisitor(
        private val functionName: String,
        private val context: CheckerContext,
        private val reporter: DiagnosticReporter
    ) : FirVisitorVoid() {

        override fun visitElement(element: FirElement) {
            element.acceptChildren(this)
        }

        override fun visitThrowExpression(throwExpression: FirThrowExpression) {
            if (throwExpression.source?.kind !is KtFakeSourceElementKind) {
                reporter.reportOn(
                    throwExpression.source,
                    KotlinVerifierErrors.THROW_EXPRESSION_IN_FUNCTION,
                    "Throw expression violates functional purity - exceptions break referential transparency and introduce side effects",
                    "in function '$functionName'",
                    context
                )
            }
            super.visitThrowExpression(throwExpression)
        }
    }
} 