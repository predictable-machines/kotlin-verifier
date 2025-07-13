package org.jetbrains.kotlin.compiler.plugin.template.fir

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.*

object KotlinVerifierErrors {
    val MUTABLE_VAR_IN_FUNCTION by error2<PsiElement, String, String>()
    val VAR_ASSIGNMENT_IN_FUNCTION by error2<PsiElement, String, String>()
    val UNIT_EXPRESSION_IN_FUNCTION by error2<PsiElement, String, String>()
}
