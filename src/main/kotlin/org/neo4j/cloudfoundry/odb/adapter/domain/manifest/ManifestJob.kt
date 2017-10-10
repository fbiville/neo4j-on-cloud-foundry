package org.neo4j.cloudfoundry.odb.adapter.domain.manifest

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.NoArgConstructorPlease

@NoArgConstructorPlease
data class ManifestJob(@Mandatory var name: String,
                       @Mandatory var release: String,
                       var consumes: Map<String, String>? = null,
                       var provides: Map<String, String>? = null,
                       @Mandatory var properties: Map<String, String>)