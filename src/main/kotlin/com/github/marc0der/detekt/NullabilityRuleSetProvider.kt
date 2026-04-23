package com.github.marc0der.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class NullabilityRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "NullabilityRuleSet"

    override fun instance(config: Config): RuleSet =
        RuleSet(
            ruleSetId,
            listOf(
                NoNullableTypes(config),
                NoElvisOperator(config),
                NoNotNullAssertion(config),
                NoSafeCall(config),
                NoPlatformTypes(config),
            ),
        )
}
