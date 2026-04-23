package io.sdkman.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class SdkmanRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "SdkmanRuleSet"

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
