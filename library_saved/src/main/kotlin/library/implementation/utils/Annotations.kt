package library.implementation.utils

import com.google.inject.BindingAnnotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@BindingAnnotation
annotation class UsersStorage

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@BindingAnnotation
annotation class BooksStorage

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@BindingAnnotation
annotation class TokensStorage

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@BindingAnnotation
annotation class LoansStorage

