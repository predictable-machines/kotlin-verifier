package org.jetbrains.kotlin.compiler.plugin.template.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.compiler.plugin.template.fir.checkers.MutableVariableChecker
import org.jetbrains.kotlin.compiler.plugin.template.fir.checkers.UnitExpressionChecker
import org.jetbrains.kotlin.compiler.plugin.template.fir.checkers.LoopExpressionChecker
import org.jetbrains.kotlin.compiler.plugin.template.fir.checkers.TopLevelMutableVariableChecker
import org.jetbrains.kotlin.compiler.plugin.template.fir.checkers.ThrowExpressionChecker

class KotlinVerifierAdditionalCheckers(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val simpleFunctionCheckers: Set<FirSimpleFunctionChecker> = setOf(
            MutableVariableChecker,
            UnitExpressionChecker,
            LoopExpressionChecker,
            ThrowExpressionChecker
        )
        
        override val propertyCheckers: Set<FirPropertyChecker> = setOf(
            TopLevelMutableVariableChecker
        )
    }
}