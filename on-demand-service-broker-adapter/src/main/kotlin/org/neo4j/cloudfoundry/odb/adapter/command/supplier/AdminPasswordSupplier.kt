package org.neo4j.cloudfoundry.odb.adapter.command.supplier

import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest

class AdminPasswordSupplier {
    fun getAdminPassword(manifest: Manifest): String? {
        return manifest.instance_groups?.get(0)?.jobs?.get(0)?.properties?.admin_password
    }
}