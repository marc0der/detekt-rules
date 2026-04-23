package com.github.marc0der.detekt

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY
)
@Retention(AnnotationRetention.BINARY)
annotation class AllowNullableTypes
