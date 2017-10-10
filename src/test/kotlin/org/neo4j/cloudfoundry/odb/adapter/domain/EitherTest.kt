package org.neo4j.cloudfoundry.odb.adapter.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.domain.Either

class EitherTest {

    @Test
    fun `mapLefts returns null when no Left`() {
        val input = listOf(Either.Right(true))

        assertThat(Either.lefts(input)).isNull()
    }


    @Test
    fun `mapLefts transforms a List of Lefts into a Left of List`() {
        val input = listOf(Either.Left(false), Either.Left(true), Either.Right("i'm not gonna be part of it"))

        val result = Either.lefts(input)

        assertThat(result).isEqualTo(Either.Left(listOf(false, true)))
    }

    @Test
    fun `flatMapLefts returns null when no Left`() {
        val input: List<Either<List<Boolean>, List<String>>> = listOf(Either.Right(listOf("whatever")))

        assertThat(Either.flattenLefts(input)).isNull()
    }

    @Test
    fun `flatMapLefts transforms a List of Left of Lists into a Left of List`() {
        val input = listOf(Either.Left(listOf(false, false)), Either.Left(listOf(true)))

        val result = Either.flattenLefts(input)

        assertThat(result).isEqualTo(Either.Left(listOf(false, false, true)))
    }

    @Test
    fun `maps List of Right values to array`() {
        val input = listOf(Either.Right("copain"), Either.Right("de soupe"))

        val result = Either.rightsArray(input)

        assertThat(result).isEqualTo(arrayOf("copain", "de soupe"))
    }
}