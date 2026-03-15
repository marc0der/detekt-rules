package io.sdkman.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

class NoNullableTypes(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Nullable types are discouraged. Use Arrow's Option instead, " +
            "or annotate with @AllowNullableTypes to suppress.",
        Debt.TWENTY_MINS,
    )

    override fun visitNullableType(nullableType: KtNullableType) {
        super.visitNullableType(nullableType)

        val enclosingDeclaration =
            nullableType.getStrictParentOfType<KtNamedFunction>()
                ?: nullableType.getStrictParentOfType<KtParameter>()
                ?: nullableType.getStrictParentOfType<KtProperty>()

        if (enclosingDeclaration != null && hasAllowAnnotation(enclosingDeclaration)) {
            return
        }

        report(
            CodeSmell(
                issue,
                Entity.from(nullableType),
                "Nullable type '${nullableType.text}' detected. " +
                    "Prefer Arrow's Option type.",
            ),
        )
    }

    private fun hasAllowAnnotation(declaration: org.jetbrains.kotlin.psi.KtAnnotated): Boolean =
        declaration.annotationEntries.any { annotation ->
            annotation.shortName?.asString() == "AllowNullableTypes"
        }
}
