package com.github.marc0der.detekt

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

    should("flag a nullable parameter type") {
        // given: a function with a nullable parameter
        val code = """
            fun process(name: String?): String = name ?: "default"
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: one finding is reported
        withClue("Expected exactly one finding for nullable parameter") {
            findings shouldHaveSize 1
        }
        withClue("Finding message should mention the nullable type") {
            findings.first().message shouldContain "String?"
        }
    }

    should("flag a nullable property type") {
        // given: a property with a nullable type
        val code = """
            val result: String? = null
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: one finding is reported
        withClue("Expected exactly one finding for nullable property") {
            findings shouldHaveSize 1
        }
        withClue("Finding message should mention the nullable type") {
            findings.first().message shouldContain "String?"
        }
    }

    should("flag a nullable local variable type") {
        // given: a local variable with a nullable type inside a function
        val code = """
            fun example() {
                var count: Int? = null
            }
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: one finding is reported
        withClue("Expected exactly one finding for nullable local variable") {
            findings shouldHaveSize 1
        }
        withClue("Finding message should mention the nullable type") {
            findings.first().message shouldContain "Int?"
        }
    }

    should("flag multiple nullable usages in a single file") {
        // given: code with three distinct nullable types
        val code = """
            fun getUser(id: String): String? = null
            val name: String? = null
            fun process(input: Int?): String = "done"
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: three findings are reported
        withClue("Expected three findings for three nullable usages") {
            findings shouldHaveSize 3
        }
    }

    should("report no findings for non-nullable code") {
        // given: code with no nullable types
        val code = """
            fun greet(name: String): String = "Hello, ${'$'}name"
            val count: Int = 42
            fun add(a: Int, b: Int): Int = a + b
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected no findings for non-nullable code") {
            findings shouldHaveSize 0
        }
    }

    should("suppress findings when function is annotated with @AllowNullableTypes") {
        // given: a function annotated with @AllowNullableTypes
        val code = """
            annotation class AllowNullableTypes

            @AllowNullableTypes
            fun legacyBridge(): String? = null
        """.trimIndent()

        // when: the rule is applied
        val findings = rule.lint(code)

        // then: no findings are reported
        withClue("Expected no findings when @AllowNullableTypes is present") {
            findings shouldHaveSize 0
        }
    }
})
