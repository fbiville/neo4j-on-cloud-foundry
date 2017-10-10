package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceRelease
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestRelease

class ReleaseGenerator {

    fun generateRelease(release: ServiceRelease): ManifestRelease {
        return ManifestRelease(release.name, release.version)
    }
}