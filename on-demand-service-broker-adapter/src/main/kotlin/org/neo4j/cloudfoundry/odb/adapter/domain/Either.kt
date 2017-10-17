package org.neo4j.cloudfoundry.odb.adapter.domain

sealed class Either<out L, out R> {
    abstract fun isRight(): Boolean

    data class Left<out L>(val value: L): Either<L, Nothing>() {
        override fun isRight(): Boolean = false
    }

    data class Right<out R>(val value: R): Either<Nothing, R>() {
        override fun isRight(): Boolean = true
    }

    companion object {
        fun <L,R> lefts(results: List<Either<L, R>>): Left<List<L>>? {
            val leftResults = results.filter { !it.isRight() }
            if (leftResults.isEmpty()) {
                return null
            }
            return Left(leftResults.map { (it as Left<L>).value })
        }

        fun <L, R> flattenLefts(results: List<Either<List<L>, R>>): Left<List<L>>? {
            val leftResults = results.filter { ! it.isRight() }
            if (leftResults.isEmpty()) {
                return null
            }
            return Left(leftResults.flatMap { (it as Left<List<L>>).value })
        }

        inline fun <L, reified R> rightsArray(results: List<Either<L, R>>): Array<R> {
            val rightValues: List<R> = results.map { (it as Right<R>).value }
            return rightValues.toTypedArray()
        }
    }
}