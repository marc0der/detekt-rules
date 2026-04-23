# detekt-rules

Custom [Detekt](https://detekt.dev) rules for enforcing strict functional programming conventions in Kotlin.

## Rules

### NoNullableTypes

Flags any use of explicit nullable types (`?`) in Kotlin source code. Encourages the use of Arrow's `Option` type instead.

The rule detects nullable types in:
- Function return types (`fun foo(): String?`)
- Function parameters (`fun foo(x: String?)`)
- Class/object properties (`val x: String?`)
- Local variable declarations (`var y: Int?`)

## Installation

Add the JitPack repository and the dependency to your build:

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

## Suppression with @AllowNullableTypes

For cases where nullable types are unavoidable (e.g. Java interop), annotate the declaration with `@AllowNullableTypes`:

```kotlin
import com.github.marc0der.detekt.AllowNullableTypes

@AllowNullableTypes
fun legacyJavaBridge(): String? = javaClient.getValue()
```

The annotation can be applied to:
- Functions
- Value parameters
- Properties

Local variables cannot be suppressed — they must be refactored to use non-nullable types.
