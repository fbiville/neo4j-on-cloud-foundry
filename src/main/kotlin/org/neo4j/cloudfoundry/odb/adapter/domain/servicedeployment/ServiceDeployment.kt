package org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import java.util.Arrays

data class ServiceDeployment(@Mandatory val deployment_name:String?,
                             @Mandatory val releases: Array<ServiceRelease>?,
                             @Mandatory val stemcell: ServiceStemcell?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceDeployment

        if (deployment_name != other.deployment_name) return false
        if (!Arrays.equals(releases, other.releases)) return false
        if (stemcell != other.stemcell) return false

        return true
    }

    override fun hashCode(): Int {
        var result = deployment_name?.hashCode() ?: 0
        result = 31 * result + (releases?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (stemcell?.hashCode() ?: 0)
        return result
    }
}

