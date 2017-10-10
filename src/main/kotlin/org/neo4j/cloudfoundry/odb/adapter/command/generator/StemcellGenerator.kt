package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceStemcell
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestStemcell

class StemcellGenerator {

    fun generateStemcell(serviceStemcell: ServiceStemcell): ManifestStemcell {
        return ManifestStemcell(GeneratorConfig.singleStemcellName, version = serviceStemcell.stemcell_version, os = serviceStemcell.stemcell_os)
    }
}