package org.neo4j.cloudfoundry.odb.adapter.domain.manifest

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.NoArgConstructorPlease

@NoArgConstructorPlease
data class ManifestRelease(@Mandatory var name: String?,
                           @Mandatory var version: String?,
                           var url: String? = null,
                           var sha1: String? = null)