package org.neo4j.cloudfoundry.odb.adapter.domain.manifest

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.NoArgConstructorPlease
import org.neo4j.cloudfoundry.odb.adapter.domain.update.Update
import java.util.Arrays

@NoArgConstructorPlease
data class Manifest(@Mandatory var name: String?,
                    var direction_uuid: String? = null,
                    var features: Map<String, String>? = null,
                    @Mandatory var releases: Array<ManifestRelease>?,
                    @Mandatory var stemcells: Array<ManifestStemcell>?,
                    @Mandatory var update: Update?,
                    @Mandatory var instance_groups: Array<ManifestInstanceGroup>?,
                    @Mandatory var properties: Map<String, String>,
                    var variables: Array<ManifestVariable>? = null) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Manifest

        if (name != other.name) return false
        if (direction_uuid != other.direction_uuid) return false
        if (features != other.features) return false
        if (!Arrays.equals(releases, other.releases)) return false
        if (!Arrays.equals(stemcells, other.stemcells)) return false
        if (update != other.update) return false
        if (!Arrays.equals(instance_groups, other.instance_groups)) return false
        if (properties != other.properties) return false
        if (!Arrays.equals(variables, other.variables)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (direction_uuid?.hashCode() ?: 0)
        result = 31 * result + (features?.hashCode() ?: 0)
        result = 31 * result + (releases?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (stemcells?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (update?.hashCode() ?: 0)
        result = 31 * result + (instance_groups?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (properties?.hashCode() ?: 0)
        result = 31 * result + (variables?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }
}

