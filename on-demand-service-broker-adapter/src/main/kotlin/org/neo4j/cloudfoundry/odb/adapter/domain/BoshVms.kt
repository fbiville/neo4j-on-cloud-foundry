package org.neo4j.cloudfoundry.odb.adapter.domain

import java.util.Arrays

data class BoshVms(@Mandatory var neo4j: Array<String>?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoshVms

        if (!Arrays.equals(neo4j, other.neo4j)) return false

        return true
    }

    override fun hashCode(): Int {
        return neo4j?.let { Arrays.hashCode(it) } ?: 0
    }
}