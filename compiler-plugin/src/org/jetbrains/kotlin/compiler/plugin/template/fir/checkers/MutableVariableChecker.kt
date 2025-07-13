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

object MutableVariableChecker : FirSimpleFunctionChecker(MppCheckerKind.Common) {
    override fun check(declaration: FirSimpleFunction, context: CheckerContext, reporter: DiagnosticReporter) {
        val functionName = declaration.name.asString()
        val visitor = MutableVariableVisitor(functionName, context, reporter)
        declaration.body?.accept(visitor)
    }
    
    private class MutableVariableVisitor(
        private val functionName: String,
        private val context: CheckerContext,
        private val reporter: DiagnosticReporter
    ) : FirVisitorVoid() {
        
        override fun visitElement(element: FirElement) {
            element.acceptChildren(this)
        }
        
        override fun visitProperty(property: FirProperty) {
            if (property.source?.kind !is KtFakeSourceElementKind && !property.isVal) {
                reporter.reportOn(
                    property.source,
                    KotlinVerifierErrors.MUTABLE_VAR_IN_FUNCTION,
                    property.name.asString(),
                    functionName,
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
                    variable.name.asString(),
                    functionName,
                    context
                )
            }
            super.visitVariable(variable)
        }
        
        override fun visitVariableAssignment(variableAssignment: FirVariableAssignment) {
            val lValue = variableAssignment.lValue
            if (lValue is FirQualifiedAccessExpression) {
                val resolvedSymbol = lValue.calleeReference.toResolvedVariableSymbol()
                
                when (resolvedSymbol) {
                    is FirVariableSymbol<*> -> {
                        reporter.reportOn(
                            variableAssignment.source,
                            KotlinVerifierErrors.VAR_ASSIGNMENT_IN_FUNCTION,
                            resolvedSymbol.name.asString(),
                            functionName,
                            context
                        )
                    }
                    null -> {
                        // Could not resolve symbol, skip
                    }
                }
            }
            
            super.visitVariableAssignment(variableAssignment)
        }
    }
}
