package org.neo4j.cloudfoundry.odb.adapter.domain.manifest

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.NoArgConstructorPlease
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceMigration
import java.util.Arrays

@NoArgConstructorPlease
data class ManifestInstanceGroup(@Mandatory var name: String?,
                                 @Mandatory var azs: Array<String>?,
                                 @Mandatory var instances: Int?,
                                 @Mandatory var jobs: Array<ManifestJob>?,
                                 @Mandatory var vm_type: String?,
                                 var vm_extensions: Array<String>? = null,
                                 @Mandatory var stemcell: String?,
                                 var persistent_disk_type: String? = null,
                                 @Mandatory var networks: Array<ManifestNetwork>?,
                                 var update: Map<String, String>? = null,
                                 var migrated_from: Array<ServiceMigration>? = null,
                                 var lifecycle: String? = null,
                                 var properties: Map<String, String>? = null,
                                 var env: Map<String, String>? = null) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ManifestInstanceGroup

        if (name != other.name) return false
        if (!Arrays.equals(azs, other.azs)) return false
        if (instances != other.instances) return false
        if (!Arrays.equals(jobs, other.jobs)) return false
        if (vm_type != other.vm_type) return false
        if (!Arrays.equals(vm_extensions, other.vm_extensions)) return false
        if (stemcell != other.stemcell) return false
        if (persistent_disk_type != other.persistent_disk_type) return false
        if (!Arrays.equals(networks, other.networks)) return false
        if (update != other.update) return false
        if (!Arrays.equals(migrated_from, other.migrated_from)) return false
        if (lifecycle != other.lifecycle) return false
        if (properties != other.properties) return false
        if (env != other.env) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (azs?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (instances ?: 0)
        result = 31 * result + (jobs?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (vm_type?.hashCode() ?: 0)
        result = 31 * result + (vm_extensions?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (stemcell?.hashCode() ?: 0)
        result = 31 * result + (persistent_disk_type?.hashCode() ?: 0)
        result = 31 * result + (networks?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (update?.hashCode() ?: 0)
        result = 31 * result + (migrated_from?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (lifecycle?.hashCode() ?: 0)
        result = 31 * result + (properties?.hashCode() ?: 0)
        result = 31 * result + (env?.hashCode() ?: 0)
        return result
    }
}