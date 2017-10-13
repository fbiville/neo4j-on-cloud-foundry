package org.neo4j.cloudfoundry.odb.adapter.command

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.command.error.JobNotFound
import org.neo4j.cloudfoundry.odb.adapter.command.error.MigrationNotSupported
import org.neo4j.cloudfoundry.odb.adapter.command.generator.ManifestGenerator
import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.domain.RequestParameters
import org.neo4j.cloudfoundry.odb.adapter.serializer.ManifestRepresenter
import org.neo4j.cloudfoundry.odb.adapter.serializer.YamlSerializer

class GenerateManifestCommandTest {

    private val manifestGenerator = mock<ManifestGenerator>()
    private val yamlSerializer = mock<YamlSerializer>()
    private val subject = GenerateManifestCommand(manifestGenerator, yamlSerializer)

    @Before
    fun prepare() {
        subject.serviceDeployment = Fixtures.serviceDeployment
        subject.plan = Fixtures.plan
        subject.requestParams = RequestParameters(mapOf())
    }

    @Test
    fun `outputs an error when the generator fails`() {
        val errors = listOf(JobNotFound("neo4j"), MigrationNotSupported)
        whenever(manifestGenerator.generateManifest(Fixtures.serviceDeployment, Fixtures.plan, mapOf()))
                .thenReturn(Either.Left(errors))
        val result = subject.execute()

        assertThat(result).isEqualTo(CommandOutput.Error(
                1, "Could not find job for instance group name neo4j" +
                        "\nServiceMigration is not supported yet"))
    }

    @Test
    fun `outputs a manifest`() {
        val manifest = Either.Right(Fixtures.manifest)
        whenever(manifestGenerator.generateManifest(Fixtures.serviceDeployment, Fixtures.plan, mapOf()))
                .thenReturn(manifest)

        val expectedOutput = "result: yay\nyou: win"
        whenever(yamlSerializer.serialize(any(), eq(manifest.value))).thenReturn(expectedOutput)

        val result = subject.execute()

        assertThat(result).isEqualTo(CommandOutput.Standard(expectedOutput))
    }
}