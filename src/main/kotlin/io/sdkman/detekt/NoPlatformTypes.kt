package io.sdkman.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.isFlexible

@RequiresTypeResolution
class NoPlatformTypes(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Platform types from Java interop silently erase nullability guarantees. " +
            "Declare an explicit Kotlin type (wrap absence in Arrow's Option), " +
            "or annotate with @AllowNullableUsage to suppress.",
        Debt.TWENTY_MINS,
    )

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        if (property.typeReference != null) return
        if (bindingContext == BindingContext.EMPTY) return
        if (property.isSuppressedFromNullableUsage()) return

        val descriptor = bindingContext[BindingContext.VARIABLE, property] ?: return
        if (descriptor.type.isFlexible()) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(property),
                    "Property '${property.name}' has an inferred platform type. " +
                        "Declare an explicit Kotlin type and wrap absence in Arrow's Option.",
                ),
            )
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (function.typeReference != null) return
        if (function.hasBlockBody()) return
        if (bindingContext == BindingContext.EMPTY) return
        if (function.isSuppressedFromNullableUsage()) return

        val descriptor = bindingContext[BindingContext.FUNCTION, function] ?: return
        val returnType = descriptor.returnType ?: return
        if (returnType.isFlexible()) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(function),
                    "Function '${function.name}' has an inferred platform return type. " +
                        "Declare an explicit Kotlin return type and wrap absence in Arrow's Option.",
                ),
            )
        }
    }
}
