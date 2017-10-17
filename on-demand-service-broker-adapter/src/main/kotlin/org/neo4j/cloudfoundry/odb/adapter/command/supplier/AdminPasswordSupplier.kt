package org.neo4j.cloudfoundry.odb.adapter.command.supplier

import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest

class AdminPasswordSupplier {
    fun getAdminPassword(manifest: Manifest): String? {
        return manifest.properties?.admin_password
    }
}