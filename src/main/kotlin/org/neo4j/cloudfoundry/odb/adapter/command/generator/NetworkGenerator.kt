package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestNetwork

class NetworkGenerator {
    fun generateNetwork(network: String): ManifestNetwork {
        return ManifestNetwork(network)
    }
}