package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.assertj.core.api.Assertions.*
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestNetwork

class NetworkGeneratorTest {

    val subject = NetworkGenerator()

    @Test
    fun `generates a ManifestNetwork`() {
        assertThat(subject.generateNetwork("default")).isEqualTo(ManifestNetwork("default"))
    }
}