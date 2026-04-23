package com.github.marc0der.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtPostfixExpression

class NoNotNullAssertion(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "The not-null assertion (!!) trades a compile-time guarantee for a runtime crash. " +
            "Model absence explicitly with Arrow's Option, " +
            "or annotate with @AllowNullableUsage to suppress.",
        Debt.TWENTY_MINS,
    )

    override fun visitPostfixExpression(expression: KtPostfixExpression) {
        super.visitPostfixExpression(expression)

        if (expression.operationToken != KtTokens.EXCLEXCL) return
        if (expression.isSuppressedFromNullableUsage()) return

        report(
            CodeSmell(
                issue,
                Entity.from(expression),
                "Not-null assertion '!!' detected. Prefer modelling absence with " +
                    "Arrow's Option and handling the None case explicitly.",
            ),
        )
    }
}
