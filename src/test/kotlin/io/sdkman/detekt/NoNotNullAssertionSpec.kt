package io.sdkman.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain

class NoNotNullAssertionSpec : ShouldSpec({

    val rule = NoNotNullAssertion(Config.empty)

    should("flag a simple not-null assertion") {
        // given: code using !!
        val code = """
            fun length(name: String?): Int = name!!.length
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: one finding is reported
        withClue("Expected exactly one finding for !!") {
            findings shouldHaveSize 1
        }
        withClue("Finding message should reference the !! operator") {
            findings.first().message shouldContain "!!"
        }
    }

    should("flag multiple not-null assertions in one file") {
        // given: code with several !! expressions
        val code = """
            fun a(x: String?): Int = x!!.length
            fun b(y: Int?): Int = y!! + 1
            val z: String = (null as String?)!!
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: three findings are reported
        withClue("Expected three findings for three !! expressions") {
            findings shouldHaveSize 3
        }
    }

    should("report no findings when !! is absent") {
        // given: code with no !!
        val code = """
            fun add(a: Int, b: Int): Int = a + b
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected no findings for code without !!") {
            findings shouldHaveSize 0
        }
    }

    should("suppress findings when enclosing function is annotated with @AllowNullableUsage") {
        // given: a function annotated with @AllowNullableUsage
        val code = """
            annotation class AllowNullableUsage

            @AllowNullableUsage
            fun legacyBridge(name: String?): Int = name!!.length
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected no findings when @AllowNullableUsage is present") {
            findings shouldHaveSize 0
        }
    }

    should("suppress findings when enclosing function is annotated with legacy @AllowNullableTypes") {
        // given: a function annotated with the legacy annotation
        val code = """
            annotation class AllowNullableTypes

            @AllowNullableTypes
            fun legacyBridge(name: String?): Int = name!!.length
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected no findings when legacy @AllowNullableTypes is present") {
            findings shouldHaveSize 0
        }
    }
})
