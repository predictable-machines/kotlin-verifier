package org.jetbrains.kotlin.compiler.plugin.template.fir.checkers

import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

object PureAnnotationUtils {
    
    private val PURE_ANNOTATION_FQ_NAME = FqName("org.jetbrains.kotlin.compiler.plugin.template.Pure")
    private val PURE_ANNOTATION_CLASS_ID = ClassId.topLevel(PURE_ANNOTATION_FQ_NAME)
    
    /**
     * Checks if the given file has the @Pure annotation at the file level
     */
    fun hasFileLevelPureAnnotation(context: CheckerContext): Boolean {
        val containingFile = context.containingFile
        return containingFile != null && checkFileForPureAnnotation(containingFile)
    }
    
    /**
     * Checks if the given FirFile has the @Pure annotation at the file level
     */
    private fun checkFileForPureAnnotation(file: FirFile): Boolean {
        return file.annotations.any { annotation ->
            // Check if annotation is the Pure annotation using string matching
            val annotationTypeString = annotation.annotationTypeRef.coneType.toString()
            annotationTypeString == "org/jetbrains/kotlin/compiler/plugin/template/Pure"
        }
    }
} 