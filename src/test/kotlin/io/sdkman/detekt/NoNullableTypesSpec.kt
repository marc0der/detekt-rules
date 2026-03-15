package io.sdkman.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain

class NoNullableTypesSpec : ShouldSpec({

    val rule = NoNullableTypes(Config.empty)

    should("flag a nullable return type on a function") {
        // given: a function with a nullable return type
        val code = """
            fun findUser(id: String): String? = null
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: one finding is reported
        withClue("Expected exactly one finding for nullable return type") {
            findings shouldHaveSize 1
        }
        withClue("Finding message should mention the nullable type") {
            findings.first().message shouldContain "String?"
        }
    }
})
