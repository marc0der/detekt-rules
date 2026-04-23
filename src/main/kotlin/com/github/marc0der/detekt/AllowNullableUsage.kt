package com.github.marc0der.detekt

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FILE,
)
@Retention(AnnotationRetention.BINARY)
annotation class AllowNullableUsage
