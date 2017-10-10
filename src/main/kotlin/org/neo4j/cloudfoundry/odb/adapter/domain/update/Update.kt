package org.neo4j.cloudfoundry.odb.adapter.domain.update

import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.NoArgConstructorPlease

@NoArgConstructorPlease
data class Update(@Mandatory var canaries: Int?,
                  @Mandatory var max_in_flight: Int?,
                  @Mandatory var canary_watch_time: String?,
                  @Mandatory var update_watch_time: String?,
                  var serial: Boolean? = null)