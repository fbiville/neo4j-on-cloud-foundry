package org.neo4j.cloudfoundry.odb.adapter.command.generator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures
import org.neo4j.cloudfoundry.odb.adapter.command.error.JobNotFound
import org.neo4j.cloudfoundry.odb.adapter.command.error.ManifestCommandError
import org.neo4j.cloudfoundry.odb.adapter.command.error.MigrationNotSupported
import org.neo4j.cloudfoundry.odb.adapter.command.error.ReleaseNotFound
import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Assertions.assertThat
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestProperties
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.Plan

class ManifestGeneratorTest {

    private val instanceGroupGenerator = mock<InstanceGroupGenerator>()
    private val stemcellGenerator = mock<StemcellGenerator>()
    private val releaseGenerator = mock<ReleaseGenerator>()
    private val passwordGenerator = mock<PasswordGenerator>()
    private val subject = ManifestGenerator(
            instanceGroupGenerator,
            stemcellGenerator,
            releaseGenerator,
            passwordGenerator)
    private val password = "c0p1-2-soop!"

    @Before
    fun prepare() {
        whenever(instanceGroupGenerator.generateInstanceGroup(Fixtures.instanceGroup, Fixtures.serviceDeployment))
                .thenReturn(Either.Right(Fixtures.manifestInstanceGroup))
        whenever(stemcellGenerator.generateStemcell(Fixtures.stemcell))
                .thenReturn(Fixtures.manifestStemcell)
        whenever(releaseGenerator.generateRelease(Fixtures.release))
                .thenReturn(Fixtures.manifestRelease)
        whenever(passwordGenerator.generate()).thenReturn(password)
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
                .hasProperties(ManifestProperties(password))
    }

    @Test
    fun `fails if instance groups cannot be generated`() {
        val listOfErrors = listOf(JobNotFound("neo4j"), ReleaseNotFound("neo4j"))
        whenever(instanceGroupGenerator.generateInstanceGroup(
                Fixtures.instanceGroup,
                Fixtures.serviceDeployment)
        ).thenReturn(Either.Left(listOfErrors))

        val errors = subject.generateManifest(Fixtures.serviceDeployment, Fixtures.plan, mapOf())
                as Either.Left<List<ManifestCommandError>>

        assertThat(errors.value).isEqualTo(listOfErrors)
    }

    @Test
    fun `fails if previous manifest is provided`() {
        val previousManifest = mock<Manifest>()

        val errors = subject.generateManifest(Fixtures.serviceDeployment, Fixtures.plan, mapOf(), previousManifest)
                as Either.Left<List<ManifestCommandError>>

        assertThat(errors.value).containsExactly(MigrationNotSupported)
    }

    @Test
    fun `fails if previous plan is provided`() {
        val previousPlan = mock<Plan>()

        val errors = subject.generateManifest(Fixtures.serviceDeployment, Fixtures.plan, mapOf(), previousPlan = previousPlan)
                as Either.Left<List<ManifestCommandError>>

        assertThat(errors.value).containsExactly(MigrationNotSupported)
    }
}