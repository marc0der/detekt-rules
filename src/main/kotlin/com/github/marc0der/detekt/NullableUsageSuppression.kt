package com.github.marc0der.detekt

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtFile

private val SUPPRESSION_ANNOTATION_NAMES = setOf("AllowNullableUsage", "AllowNullableTypes")

internal fun PsiElement.isSuppressedFromNullableUsage(): Boolean {
    var current: PsiElement? = this
    while (current != null) {
        if (current is KtAnnotated && current.hasSuppressionAnnotation()) {
            return true
        }
        if (current is KtFile && current.fileAnnotationsSuppress()) {
            return true
        }
        current = current.parent
    }
    return false
}

private fun KtAnnotated.hasSuppressionAnnotation(): Boolean =
    annotationEntries.any { entry ->
        entry.shortName?.asString() in SUPPRESSION_ANNOTATION_NAMES
    }

private fun KtFile.fileAnnotationsSuppress(): Boolean =
    fileAnnotationList?.annotationEntries?.any { entry ->
        entry.shortName?.asString() in SUPPRESSION_ANNOTATION_NAMES
    } == true
