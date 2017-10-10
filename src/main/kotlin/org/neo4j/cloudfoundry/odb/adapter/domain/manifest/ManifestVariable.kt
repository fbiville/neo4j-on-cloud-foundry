package org.neo4j.cloudfoundry.odb.adapter.domain.manifest

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.NoArgConstructorPlease

@NoArgConstructorPlease
data class ManifestVariable(@Mandatory var name: String?,
                            @Mandatory var type: String?,
                            var options: Map<String, String>? = null)