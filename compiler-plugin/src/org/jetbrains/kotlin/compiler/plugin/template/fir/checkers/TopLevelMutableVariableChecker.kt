package org.jetbrains.kotlin.compiler.plugin.template.fir.checkers

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.compiler.plugin.template.fir.KotlinVerifierErrors
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.declarations.FirProperty

/**
 * Checker that reports an error whenever a top-level mutable variable (var) is found
 * in a file annotated with `@Pure`.
 *
 * Top-level mutable variables introduce global state which violates functional purity.
 */
object TopLevelMutableVariableChecker : FirPropertyChecker(MppCheckerKind.Common) {
    
    override fun check(declaration: FirProperty, context: CheckerContext, reporter: DiagnosticReporter) {
        // Only run purity checks if @Pure annotation is present
        if (PureAnnotationUtils.hasFileLevelPureAnnotation(context)) {
            // Check if this is a top-level mutable property
            if (declaration.source?.kind !is KtFakeSourceElementKind && 
                !declaration.isVal && 
                context.containingDeclarations.size <= 1) { // Top-level means no containing class/object
                
                reporter.reportOn(
                    declaration.source,
                    KotlinVerifierErrors.TOP_LEVEL_MUTABLE_VAR,
                    "Top-level mutable variable '${declaration.name.asString()}' violates functional purity - global mutable state is not allowed",
                    "in pure file",
                    context
                )
            }
        }
    }
} 