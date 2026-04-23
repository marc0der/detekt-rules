package com.github.marc0der.detekt

import io.github.detekt.test.utils.KotlinCoreEnvironmentWrapper
import io.github.detekt.test.utils.createEnvironment
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize

class NoPlatformTypesSpec : ShouldSpec({

    lateinit var envWrapper: KotlinCoreEnvironmentWrapper
    val rule = NoPlatformTypes(Config.empty)

    beforeSpec { envWrapper = createEnvironment() }
    afterSpec { envWrapper.dispose() }

    should("flag a property whose inferred type is a Java platform type") {
        // given: a property initialised from a Java-interop call without an explicit type
        val code = """
            val home = System.getenv("HOME")
        """.trimIndent()

        // when: the rule is applied with type resolution
        val findings = rule.compileAndLintWithContext(envWrapper.env, code)

        // then: one finding is reported
        withClue("Expected one finding for platform-typed property") {
            findings shouldHaveSize 1
        }
    }

    should("flag a function whose inferred return type is a Java platform type") {
        // given: an expression-body function returning a Java-interop call without an explicit return type
        val code = """
            fun home() = System.getenv("HOME")
        """.trimIndent()

        // when: the rule is applied with type resolution
        val findings = rule.compileAndLintWithContext(envWrapper.env, code)

        // then: one finding is reported
        withClue("Expected one finding for platform-typed function return") {
            findings shouldHaveSize 1
        }
    }

    should("not flag a property with an explicit non-nullable Kotlin type") {
        // given: a property with an explicit type annotation
        val code = """
            val home: String = System.getenv("HOME") ?: ""
        """.trimIndent()

        // when: the rule is applied with type resolution
        val findings = rule.compileAndLintWithContext(envWrapper.env, code)

        // then: no findings are reported by this rule
        withClue("Explicit type should disarm the platform-type rule") {
            findings shouldHaveSize 0
        }
    }

    should("not flag inferred pure-Kotlin types") {
        // given: a property inferred from a Kotlin expression
        val code = """
            val greeting = "hello"
            fun sum(a: Int, b: Int) = a + b
        """.trimIndent()

        // when: the rule is applied with type resolution
        val findings = rule.compileAndLintWithContext(envWrapper.env, code)

        // then: no findings are reported
        withClue("Pure Kotlin inference should not be flagged") {
            findings shouldHaveSize 0
        }
    }

    should("suppress findings when annotated with @AllowNullableUsage") {
        // given: a platform-typed property whose enclosing file carries the annotation
        val code = """
            annotation class AllowNullableUsage

            @AllowNullableUsage
            val home = System.getenv("HOME")
        """.trimIndent()

        // when: the rule is applied with type resolution
        val findings = rule.compileAndLintWithContext(envWrapper.env, code)

        // then: no findings are reported
        withClue("Expected no findings when @AllowNullableUsage is present") {
            findings shouldHaveSize 0
        }
    }
})
