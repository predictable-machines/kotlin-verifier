package org.jetbrains.kotlin.compiler.plugin.template.fir.checkers

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.compiler.plugin.template.fir.KotlinVerifierErrors
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.FirVariable
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirVariableAssignment
import org.jetbrains.kotlin.fir.references.toResolvedVariableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid
import org.jetbrains.kotlin.fir.references.FirReference
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.KtSourceElement

object MutableVariableChecker : FirSimpleFunctionChecker(MppCheckerKind.Common) {
    
    override fun check(declaration: FirSimpleFunction, context: CheckerContext, reporter: DiagnosticReporter) {
        // Only run purity checks if @Pure annotation is present
        if (PureAnnotationUtils.hasFileLevelPureAnnotation(context)) {
            val functionName = declaration.name.asString()
            val visitor = MutableVariableVisitor(functionName, context, reporter)
            declaration.body?.accept(visitor)
        }
    }
    
    private class MutableVariableVisitor(
        private val functionName: String,
        private val context: CheckerContext,
        private val reporter: DiagnosticReporter
    ) : FirVisitorVoid() {
        // Keep track of sources we've already reported on to avoid duplicate diagnostics
        private val reportedOffsets = mutableSetOf<Int>()
        
        override fun visitElement(element: FirElement) {
            element.acceptChildren(this)
        }
        
        override fun visitProperty(property: FirProperty) {
            if (property.source?.kind !is KtFakeSourceElementKind && !property.isVal) {
                reporter.reportOn(
                    property.source,
                    KotlinVerifierErrors.MUTABLE_VAR_IN_FUNCTION,
                    "Mutable variable '${property.name.asString()}' violates functional purity - functions should not declare mutable state",
                    "in function '$functionName'",
                    context
                )
            }
            super.visitProperty(property)
        }
        
        override fun visitVariable(variable: FirVariable) {
            // FirVariable includes both FirProperty and local variables
            // We already handle FirProperty in visitProperty, so skip those
            if (variable !is FirProperty && variable.source?.kind !is KtFakeSourceElementKind && variable.isVar) {
                reporter.reportOn(
                    variable.source,
                    KotlinVerifierErrors.MUTABLE_VAR_IN_FUNCTION,
                    "Mutable variable '${variable.name.asString()}' violates functional purity - functions should not declare mutable state",
                    "in function '$functionName'",
                    context
                )
            }
            super.visitVariable(variable)
        }
        
        override fun visitVariableAssignment(variableAssignment: FirVariableAssignment) {
            // Avoid double-reporting: in operator assignments like "+=" the compiler
            // generates a synthetic (fake) [FirVariableAssignment] after desugaring the
            // operator function call. We skip such synthetic nodes because the original
            // user-written expression will be reported through [visitFunctionCall].

            // We no longer skip synthetic assignments; de-duplication is handled via offsets.

            val start = variableAssignment.source?.startOffset ?: -1
            // Resolve the symbol on the left-hand side of the assignment so we can report
            val resolvedSymbol = when (val lValue = variableAssignment.lValue) {
                is FirQualifiedAccessExpression -> lValue.calleeReference.toResolvedVariableSymbol()
                is FirReference -> lValue.toResolvedVariableSymbol()
                else -> null
            }

            val varName = resolvedSymbol?.name?.asString() ?: "<unknown>"
            if (resolvedSymbol == null) {
                println("DEBUG: unresolved assignment at offset $start for lValue=${variableAssignment.lValue::class.simpleName}")
            }

            if (reportedOffsets.add(start)) {
                reporter.reportOn(
                    variableAssignment.source,
                    KotlinVerifierErrors.VAR_ASSIGNMENT_IN_FUNCTION,
                    "Assignment to variable '$varName' violates immutability - functions should not modify state",
                    "in function '$functionName'",
                    context
                )
                println("DEBUG: VAR_ASSIGN emitted for $varName at offset=$start")
                // recorded via reportedOffsets
            }

            super.visitVariableAssignment(variableAssignment)
        }

        /**
         * Detect operator-based mutations such as "+=" or unary increment/decrement.
         * In a pure file these should be reported once even though they are later
         * desugared to a [FirVariableAssignment]. To avoid double-reporting we:
         *  1. Report here only when the source is *not* a fake/desugared element.
         *  2. Keep [visitVariableAssignment] for plain `=` mutations.
         */
        override fun visitFunctionCall(functionCall: FirFunctionCall) {
            if (functionCall.source?.kind is KtFakeSourceElementKind) {
                // Synthetic; the matching real assignment will be handled elsewhere.
                super.visitFunctionCall(functionCall)
                return
            }

            val calleeName = functionCall.calleeReference.name.asString()
            val mutatingOperatorNames = setOf(
                "plusAssign", "minusAssign", "timesAssign", "divAssign", "remAssign",
                "inc", "dec"
            )

            val fStart = functionCall.source?.startOffset ?: -1
            if (calleeName in mutatingOperatorNames && reportedOffsets.add(fStart)) {
                val receiver = functionCall.dispatchReceiver ?: functionCall.explicitReceiver
                if (receiver is FirQualifiedAccessExpression) {
                    val symbol = receiver.calleeReference.toResolvedVariableSymbol()
                    if (symbol is FirVariableSymbol<*>) {
                        reporter.reportOn(
                            functionCall.source,
                            KotlinVerifierErrors.VAR_ASSIGNMENT_IN_FUNCTION,
                            "Assignment to variable '${symbol.name.asString()}' via operator '$calleeName' violates immutability - functions should not modify state",
                            "in function '$functionName'",
                            context
                        )
                        println("DEBUG: OPER_ASSIGN at offset=$fStart via $calleeName for ${symbol.name.asString()}")
                        // recorded via reportedOffsets
                    }
                }
            }

            super.visitFunctionCall(functionCall)
        }
    }
}
