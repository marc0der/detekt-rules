package io.sdkman.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNullableType

class NoNullableTypes(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Nullable types are discouraged. Use Arrow's Option instead, " +
            "or annotate with @AllowNullableUsage to suppress.",
        Debt.TWENTY_MINS,
    )

    override fun visitNullableType(nullableType: KtNullableType) {
        super.visitNullableType(nullableType)

        if (nullableType.isSuppressedFromNullableUsage()) return

        report(
            CodeSmell(
                issue,
                Entity.from(nullableType),
                "Nullable type '${nullableType.text}' detected. " +
                    "Prefer Arrow's Option type.",
            ),
        )
    }
}
