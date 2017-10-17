package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestStemcell
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceStemcell

class ServiceStemcellGeneratorTest {
    @Test
    fun `generates a stemcell`() {
        val stemcell = ServiceStemcell("ubuntu-trusty", "3445.11")
        val subject = StemcellGenerator()

        assertThat(subject.generateStemcell(stemcell)).isEqualTo(ManifestStemcell("default", "ubuntu-trusty", "3445.11"))
    }
}