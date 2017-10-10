package org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory

data class ServiceMigration(@Mandatory val name: String?, @Mandatory val az: String?)