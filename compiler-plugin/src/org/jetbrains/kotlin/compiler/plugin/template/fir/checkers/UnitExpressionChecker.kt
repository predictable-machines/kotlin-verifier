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
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.types.isUnit
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

object UnitExpressionChecker : FirSimpleFunctionChecker(MppCheckerKind.Common) {
    
    override fun check(declaration: FirSimpleFunction, context: CheckerContext, reporter: DiagnosticReporter) {
        // Only run purity checks if @Pure annotation is present
        if (PureAnnotationUtils.hasFileLevelPureAnnotation(context)) {
            val functionName = declaration.name.asString()
            val visitor = UnitExpressionVisitor(functionName, context, reporter)
            declaration.body?.accept(visitor)
        }
    }
    
    private class UnitExpressionVisitor(
        private val functionName: String,
        private val context: CheckerContext,
        private val reporter: DiagnosticReporter
    ) : FirVisitorVoid() {
        
        override fun visitElement(element: FirElement) {
            element.acceptChildren(this)
        }
        
        override fun visitFunctionCall(functionCall: FirFunctionCall) {
            // Check if this function call returns Unit and is used as an expression
            if (functionCall.source?.kind !is KtFakeSourceElementKind) {
                val resolvedSymbol = functionCall.calleeReference.toResolvedCallableSymbol()
                
                when (resolvedSymbol) {
                    is FirFunctionSymbol<*> -> {
                        val returnTypeRef = resolvedSymbol.resolvedReturnTypeRef
                        val returnType = returnTypeRef.coneType
                        if (returnType.isUnit) {
                            // Skip suspend functions returning Unit; they are allowed
                            @OptIn(org.jetbrains.kotlin.fir.symbols.SymbolInternals::class)
                            val isSuspendFunction = resolvedSymbol.fir.status.isSuspend
                            if (isSuspendFunction) {
                                // Suspend functions are permitted; do not report
                                super.visitFunctionCall(functionCall)
                                return
                            }
                            val functionNameCalled = resolvedSymbol.name.asString()
                            
                            reporter.reportOn(
                                functionCall.source,
                                KotlinVerifierErrors.UNIT_EXPRESSION_IN_FUNCTION,
                                "Function call '$functionNameCalled()' returning Unit indicates side effects - functions should be pure expressions",
                                "in function '$functionName'",
                                context
                            )
                        }
                    }
                    null -> {
                        // Could not resolve symbol, skip
                    }
                }
            }
            super.visitFunctionCall(functionCall)
        }
        
        override fun visitBlock(block: FirBlock) {
            // Visit all statements in the block
            super.visitBlock(block)
        }
    }
} 