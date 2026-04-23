# Detekt Custom Rule: NoNullableTypes

## Context

You are building a custom Detekt rule library for the `marc0der/detekt-rules` repository. The repo has been initialised as a blank repository using the [detekt-custom-rule-template](https://github.com/detekt/detekt-custom-rule-template) as a reference. It will be distributed via JitPack.

The purpose of this library is to enforce strict functional programming conventions — specifically, prohibiting Kotlin nullable types in favour of Arrow's `Option`.

## Objective

Implement a single Detekt rule — `NoNullableTypes` — that flags any use of explicit nullable types (`?`) anywhere in Kotlin source code, with a narrow suppression escape hatch via a custom annotation.

## Acceptance Criteria

- The rule `NoNullableTypes` is implemented and correctly identifies nullable type usage in all of the following positions:
  - Function return types (e.g. `fun foo(): String?`)
  - Function parameters (e.g. `fun foo(x: String?)`)
  - Class/object properties (e.g. `val x: String?`)
  - Local variable declarations (e.g. `var y: Int?`) — note: `@AllowNullableTypes` cannot suppress these; local vars must be refactored
- A custom annotation `@AllowNullableTypes` is defined in the same library. When this annotation is applied to a declaration, the rule does **not** flag it.
- The rule is enabled by default in `config/config.yml`.
- The library is structured for JitPack distribution (correct `group`, `version`, `maven-publish` setup).
- All rule logic is covered by unit tests using the Detekt test utilities and Kotest assertions.

## Rules

- [kotest.md](https://github.com/sdkman/sdkman-state/blob/main/rules/kotest.md) — apply all rules from this file for all test code, especially RULE-001 (ShouldSpec exclusively), RULE-101 (name test classes as `Spec`), RULE-102 (given/when/then comments), RULE-103 (descriptive test names), and RULE-104 (use `withClue` for assertions).

## Domain Model

```kotlin
// The annotation that suppresses the rule
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY
)
@Retention(AnnotationRetention.BINARY)
annotation class AllowNullableTypes

// Example of what the rule flags (❌)
fun findUser(id: String): User? = TODO()
fun process(name: String?, age: Int?): String = TODO()
val result: String? = null
var count: Int? = null

// Example of what the rule allows (✅)
fun findUser(id: String): Option<User> = TODO()
@AllowNullableTypes fun legacyBridge(): String? = TODO()
```

## Implementation Considerations

- Use the [detekt-custom-rule-template](https://github.com/detekt/detekt-custom-rule-template) as the structural basis.
- Detekt version: **latest stable** (at time of writing, 1.23.8 — verify and use the actual latest).
- Kotlin version: latest stable.
- Gradle version: **latest stable**.
- Use **Kotest** as the test framework with `ShouldSpec` style. Replace JUnit Jupiter as the test runner with `kotest-runner-junit5` (Kotest runs on JUnit Platform).
- The rule should extend `Rule` and visit the AST using the appropriate `visit*` methods for nullable type nodes.
- Nullable types in Kotlin's AST are represented as `KtNullableType` nodes. Visit them and check if the **immediate enclosing declaration** (function, parameter, or property) is annotated with `@AllowNullableTypes`. Class-level and file-level suppression is intentionally not supported — annotations must be applied at the narrowest possible scope.
- `LOCAL_VARIABLE` is intentionally excluded from `@AllowNullableTypes` targets — Kotlin does not retain local variable annotations in bytecode, so Detekt cannot read them. Local nullable vars must be refactored, not suppressed.
- The annotation `AllowNullableTypes` should be defined in the same module under a sensible package (e.g. `com.github.marc0der.detekt`).
- Package: `com.github.marc0der.detekt`
- Group ID for JitPack: `com.github.marc0der`
- Artifact ID: `detekt-rules`
- Version: `1.0.0`

## Testing Strategy

Write unit tests using `io.gitlab.arturbosch.detekt:detekt-test` utilities (`lintWithContext` or `lint`). Tests must be written as Kotest `ShouldSpec` classes. Cover:

- Nullable return type → findings: 1
- Nullable parameter → findings: 1
- Nullable property → findings: 1
- Nullable local variable → findings: 1
- Multiple nullable usages in one file → findings match count
- Non-nullable code → findings: 0
- Declaration annotated with `@AllowNullableTypes` → findings: 0

## Implementation Preferences

- Follow idiomatic Kotlin throughout.
- Keep the rule implementation focused — no configuration knobs, no active/inactive toggling beyond the standard Detekt config.
- Do not implement any other rules — this library is intentionally single-rule for now.
- README should explain: what the rule does, how to add it via JitPack, and how to use `@AllowNullableTypes`.

## Examples

### Flagged (rule fires)

```kotlin
fun getUser(id: String): User? = repository.find(id)

data class Config(val timeout: Int?, val retries: Int?)

fun process(input: String?): String = input ?: "default"
```

### Allowed (rule suppressed)

```kotlin
@AllowNullableTypes
fun legacyJavaBridge(): String? = javaClient.getValue()
```

### JitPack dependency (for README)

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}

// build.gradle.kts
dependencies {
    detektPlugins("com.github.marc0der:detekt-rules:1.0.0")
}
```

## Verification Checklist

- [ ] `./gradlew test` passes with all test cases green
- [ ] `./gradlew build` produces a JAR
- [ ] `NoNullableTypes` is listed in `config/config.yml` and enabled by default
- [ ] `@AllowNullableTypes` annotation is in the published JAR (consumers can use it)
- [ ] README documents JitPack dependency and annotation usage
- [ ] JitPack build succeeds after pushing a `1.0.0` git tag
