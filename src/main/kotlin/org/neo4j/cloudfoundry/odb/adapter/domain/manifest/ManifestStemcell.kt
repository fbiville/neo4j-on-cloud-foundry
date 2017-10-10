package org.neo4j.cloudfoundry.odb.adapter.domain.manifest

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.NoArgConstructorPlease

@NoArgConstructorPlease
data class ManifestStemcell(@Mandatory var alias: String?,
                            var os: String? = null,
                            @Mandatory var version: String?,
                            var name: String? = null)