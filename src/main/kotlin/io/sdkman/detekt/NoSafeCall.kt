package io.sdkman.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression

class NoSafeCall(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "The safe-call operator (?.) propagates nullability rather than modelling it. " +
            "Prefer Arrow's Option.map { } / Option.flatMap { }, " +
            "or annotate with @AllowNullableUsage to suppress.",
        Debt.TEN_MINS,
    )

    override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
        super.visitSafeQualifiedExpression(expression)

        if (expression.isSuppressedFromNullableUsage()) return

        report(
            CodeSmell(
                issue,
                Entity.from(expression),
                "Safe-call '?.' detected. Prefer Arrow's Option.map { } or " +
                    "Option.flatMap { } to chain computations over optional values.",
            ),
        )
    }
}
