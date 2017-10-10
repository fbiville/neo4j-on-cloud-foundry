package org.neo4j.cloudfoundry.odb.adapter.domain.plan

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.update.Update
import java.util.Arrays

data class Plan(@Mandatory val instance_groups: Array<PlanInstanceGroup>?,
                @Mandatory val properties: Map<String, String>?,
                @Mandatory val update: Update?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Plan

        if (!Arrays.equals(instance_groups, other.instance_groups)) return false
        if (properties != other.properties) return false
        if (update != other.update) return false

        return true
    }

    override fun hashCode(): Int {
        var result = instance_groups?.let { Arrays.hashCode(it) } ?: 0
        result = 31 * result + (properties?.hashCode() ?: 0)
        result = 31 * result + (update?.hashCode() ?: 0)
        return result
    }
}

