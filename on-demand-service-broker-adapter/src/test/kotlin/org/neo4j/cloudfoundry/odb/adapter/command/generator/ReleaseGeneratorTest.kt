package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestRelease
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceRelease

class ReleaseGeneratorTest {
    @Test
    fun `generates a release`() {
        val release = ServiceRelease(
                name = "neo4j",
                version = "dev.1",
                jobs = arrayOf("neo4j")
        )
        val subject = ReleaseGenerator()

        assertThat(subject.generateRelease(release)).isEqualTo(ManifestRelease("neo4j", "dev.1"))
    }
}