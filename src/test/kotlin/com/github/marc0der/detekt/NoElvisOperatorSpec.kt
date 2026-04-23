package com.github.marc0der.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain

class NoElvisOperatorSpec : ShouldSpec({

    val rule = NoElvisOperator(Config.empty)

    should("flag a simple elvis expression") {
        // given: code using ?:
        val code = """
            fun greet(name: String?): String = name ?: "anon"
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: one finding is reported
        withClue("Expected exactly one finding for elvis operator") {
            findings shouldHaveSize 1
        }
        withClue("Finding should suggest Arrow's Option.getOrElse") {
            findings.first().message shouldContain "Option.getOrElse"
        }
    }

    should("flag multiple elvis usages in one file") {
        // given: code with three distinct elvis expressions
        val code = """
            fun a(x: String?): String = x ?: "a"
            fun b(y: Int?): Int = y ?: 0
            val z: String = (null as String?) ?: "z"
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: three findings are reported
        withClue("Expected three findings for three elvis expressions") {
            findings shouldHaveSize 3
        }
    }

    should("report no findings when elvis is absent") {
        // given: code with no elvis operator
        val code = """
            fun add(a: Int, b: Int): Int = a + b
            val value: Int = 42
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected no findings for code without elvis") {
            findings shouldHaveSize 0
        }
    }

    should("suppress findings when enclosing function is annotated with @AllowNullableUsage") {
        // given: a function annotated with @AllowNullableUsage
        val code = """
            annotation class AllowNullableUsage

            @AllowNullableUsage
            fun legacyBridge(name: String?): String = name ?: "anon"
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected no findings when @AllowNullableUsage is present") {
            findings shouldHaveSize 0
        }
    }

    should("suppress findings when enclosing function is annotated with legacy @AllowNullableTypes") {
        // given: a function annotated with the legacy @AllowNullableTypes
        val code = """
            annotation class AllowNullableTypes

            @AllowNullableTypes
            fun legacyBridge(name: String?): String = name ?: "anon"
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected no findings when legacy @AllowNullableTypes is present") {
            findings shouldHaveSize 0
        }
    }

    should("suppress findings for an entire annotated class") {
        // given: a class annotated with @AllowNullableUsage containing elvis usage
        val code = """
            annotation class AllowNullableUsage

            @AllowNullableUsage
            class Bridge {
                fun greet(name: String?): String = name ?: "anon"
            }
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected class-level suppression to cover nested elvis usage") {
            findings shouldHaveSize 0
        }
    }
})
