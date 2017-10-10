package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures
import org.neo4j.cloudfoundry.odb.adapter.command.error.JobNotFound
import org.neo4j.cloudfoundry.odb.adapter.command.error.ManifestCommandError
import org.neo4j.cloudfoundry.odb.adapter.command.error.MigrationNotSupported
import org.neo4j.cloudfoundry.odb.adapter.command.error.ReleaseNotFound
import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Assertions.assertThat
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.Plan

class ManifestGeneratorTest {

    private val instanceGroupGenerator = mock(InstanceGroupGenerator::class.java)
    private val stemcellGenerator = mock(StemcellGenerator::class.java)
    private val releaseGenerator = mock(ReleaseGenerator::class.java)
    private val subject = ManifestGenerator(
            instanceGroupGenerator,
            stemcellGenerator,
            releaseGenerator)

    @Before
    fun prepare() {
        `when`(instanceGroupGenerator.generateInstanceGroup(Fixtures.instanceGroup, Fixtures.serviceDeployment))
                .thenReturn(Either.Right(Fixtures.manifestInstanceGroup))
        `when`(stemcellGenerator.generateStemcell(Fixtures.stemcell))
                .thenReturn(Fixtures.manifestStemcell)
        `when`(releaseGenerator.generateRelease(Fixtures.release))
                .thenReturn(Fixtures.manifestRelease)
    }

    @Test
    fun `generates a manifest`() {
        val commandResult = subject.generateManifest(Fixtures.serviceDeployment, Fixtures.plan, mapOf()) as Either.Right<Manifest>

        val manifest = commandResult.value

        assertThat(manifest)
                .hasName("service-instance_1b42a2ea-cb9b-4dd0-8859-267048723a42")
                .hasReleases(Fixtures.manifestRelease)
                .hasStemcells(Fixtures.manifestStemcell)
                .hasInstance_groups(Fixtures.manifestInstanceGroup)
    }

    @Test
    fun `fails if instance groups cannot be generated`() {
        val listOfErrors = listOf(JobNotFound("neo4j"), ReleaseNotFound("neo4j"))
        `when`(instanceGroupGenerator.generateInstanceGroup(
                Fixtures.instanceGroup,
                Fixtures.serviceDeployment)
        ).thenReturn(Either.Left(listOfErrors))

        val errors = subject.generateManifest(Fixtures.serviceDeployment, Fixtures.plan, mapOf())
                as Either.Left<List<ManifestCommandError>>

        assertThat(errors.value).isEqualTo(listOfErrors)
    }

    @Test
    fun `fails if previous manifest is provided`() {
        val previousManifest = Mockito.mock(Manifest::class.java)

        val errors = subject.generateManifest(Fixtures.serviceDeployment, Fixtures.plan, mapOf(), previousManifest)
                as Either.Left<List<ManifestCommandError>>

        assertThat(errors.value).containsExactly(MigrationNotSupported)
    }

    @Test
    fun `fails if previous plan is provided`() {
        val previousPlan = Mockito.mock(Plan::class.java)

        val errors = subject.generateManifest(Fixtures.serviceDeployment, Fixtures.plan, mapOf(), previousPlan = previousPlan)
                as Either.Left<List<ManifestCommandError>>

        assertThat(errors.value).containsExactly(MigrationNotSupported)
    }
}