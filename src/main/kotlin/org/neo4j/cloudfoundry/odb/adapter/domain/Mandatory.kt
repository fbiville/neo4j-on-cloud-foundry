package org.neo4j.cloudfoundry.odb.adapter.domain


/**
 * Marks a field as mandatory,
 * meaning it cannot be null, blank ([String]) or empty ([Array]/[Iterable]).
 *
 * Please note that array and collection contents are not inspected.
 *
 * Annotated fields must be nullable so that the [MandatoryFieldsValidator][org.neo4j.cloudfoundry.odb.adapter.command.converter.MandatoryFieldsValidator]
 * can handle an explicit error output rather than having Kotlin crash on the
 * type incompatibility (i.e. null value of type T? to non-nullable T).
 *
 * The only theoretical drawback of this approach is that compile-time type safety
 * is forfeited, but the main usecase for this annotation is for data structures
 * deserialized at runtime.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Mandatory