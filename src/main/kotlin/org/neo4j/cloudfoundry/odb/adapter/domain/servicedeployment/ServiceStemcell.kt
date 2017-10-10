package org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory

data class ServiceStemcell(@Mandatory val stemcell_os: String?, @Mandatory val stemcell_version: String?)