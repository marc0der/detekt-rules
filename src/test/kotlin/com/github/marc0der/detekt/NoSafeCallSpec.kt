package com.github.marc0der.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain

class NoSafeCallSpec : ShouldSpec({

    val rule = NoSafeCall(Config.empty)

    should("flag a simple safe-call expression") {
        // given: code using ?.
        val code = """
            fun length(name: String?): Int? = name?.length
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: one finding is reported for the safe-call
        withClue("Expected exactly one finding for safe-call") {
            findings shouldHaveSize 1
        }
        withClue("Finding should suggest Arrow's Option.map") {
            findings.first().message shouldContain "Option.map"
        }
    }

    should("report no findings when ?. is absent") {
        // given: code with no safe-call
        val code = """
            fun length(name: String): Int = name.length
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected no findings for code without safe-call") {
            findings shouldHaveSize 0
        }
    }

    should("suppress findings when enclosing function is annotated with @AllowNullableUsage") {
        // given: a function annotated with @AllowNullableUsage
        val code = """
            annotation class AllowNullableUsage

            @AllowNullableUsage
            fun legacyBridge(name: String?): Int? = name?.length
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected no findings when @AllowNullableUsage is present") {
            findings shouldHaveSize 0
        }
    }
})
