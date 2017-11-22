package org.neo4j.cloudfoundry.odb.adapter.domain.manifest

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.NoArgConstructorPlease

@NoArgConstructorPlease
data class JobProperties(@Mandatory var admin_password: String?)