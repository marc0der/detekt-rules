package com.github.marc0der.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression

class NoElvisOperator(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "The Elvis operator (?:) reaches for a fallback against null. " +
            "Prefer Arrow's Option.getOrElse { } to model absence explicitly, " +
            "or annotate with @AllowNullableUsage to suppress.",
        Debt.TEN_MINS,
    )

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (expression.operationToken != KtTokens.ELVIS) return
        if (expression.isSuppressedFromNullableUsage()) return

        report(
            CodeSmell(
                issue,
                Entity.from(expression),
                "Elvis operator '?:' detected. Prefer Arrow's Option.getOrElse { } " +
                    "to handle absence explicitly.",
            ),
        )
    }
}
