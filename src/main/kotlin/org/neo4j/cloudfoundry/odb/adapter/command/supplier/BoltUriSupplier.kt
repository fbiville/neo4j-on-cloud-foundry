package org.neo4j.cloudfoundry.odb.adapter.command.supplier

import org.neo4j.cloudfoundry.odb.adapter.domain.BoshVms

class BoltUriSupplier {
    fun getBoltUri(boshVms: BoshVms): String? {
        val ips = boshVms.neo4j
        if (ips == null || ips.isEmpty()) {
            return null
        }
        return "bolt://${ips[0]}:7687"
    }
}