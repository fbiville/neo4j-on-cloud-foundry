package org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import java.util.Arrays

data class ServiceRelease(@Mandatory val name: String?,
                          @Mandatory val version: String?,
                          @Mandatory val jobs: Array<String>?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceRelease

        if (name != other.name) return false
        if (version != other.version) return false
        if (!Arrays.equals(jobs, other.jobs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (version?.hashCode() ?: 0)
        result = 31 * result + (jobs?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }
}