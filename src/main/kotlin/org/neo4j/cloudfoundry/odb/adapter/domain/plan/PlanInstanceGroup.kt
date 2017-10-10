package org.neo4j.cloudfoundry.odb.adapter.domain.plan

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceMigration
import java.util.Arrays

data class PlanInstanceGroup(@Mandatory val name: String?,
                             @Mandatory val vm_type: String?,
                             val vm_extensions: Array<String>? = null,
                             val persistent_disk_type: String? = null,
                             @Mandatory val networks: Array<String>?,
                             @Mandatory val instances: Int?,
                             val lifecycle: String? = null,
                             @Mandatory val azs: Array<String>?,
                             val migrated_form: Array<ServiceMigration>? = null) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlanInstanceGroup

        if (name != other.name) return false
        if (vm_type != other.vm_type) return false
        if (!Arrays.equals(vm_extensions, other.vm_extensions)) return false
        if (persistent_disk_type != other.persistent_disk_type) return false
        if (!Arrays.equals(networks, other.networks)) return false
        if (instances != other.instances) return false
        if (lifecycle != other.lifecycle) return false
        if (!Arrays.equals(azs, other.azs)) return false
        if (!Arrays.equals(migrated_form, other.migrated_form)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (vm_type?.hashCode() ?: 0)
        result = 31 * result + (vm_extensions?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (persistent_disk_type?.hashCode() ?: 0)
        result = 31 * result + (networks?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (instances ?: 0)
        result = 31 * result + (lifecycle?.hashCode() ?: 0)
        result = 31 * result + (azs?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (migrated_form?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }
}