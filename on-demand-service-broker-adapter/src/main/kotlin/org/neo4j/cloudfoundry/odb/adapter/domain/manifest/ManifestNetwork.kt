package org.neo4j.cloudfoundry.odb.adapter.domain.manifest

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.NoArgConstructorPlease
import java.util.Arrays

@NoArgConstructorPlease
data class ManifestNetwork(@Mandatory var name: String?,
                           var static_ips: Array<String>? = null,
                           var default: Array<String>? = null) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ManifestNetwork

        if (name != other.name) return false
        if (!Arrays.equals(static_ips, other.static_ips)) return false
        if (!Arrays.equals(default, other.default)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (static_ips?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (default?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }
}