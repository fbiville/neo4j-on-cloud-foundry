package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.command.error.JobNotFound
import org.neo4j.cloudfoundry.odb.adapter.command.error.ManifestCommandError
import org.neo4j.cloudfoundry.odb.adapter.command.error.ReleaseNotFound
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.PlanInstanceGroup
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceRelease
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceStemcell
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestInstanceGroup
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestJob
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestNetwork

class PlanInstanceGroupGeneratorTest {
    private val jobGenerator = mock(JobGenerator::class.java)
    private val networkGenerator = mock(NetworkGenerator::class.java)
    private val subject = InstanceGroupGenerator(jobGenerator, networkGenerator)
    private val instanceGroup = PlanInstanceGroup(
            name = "neo4j",
            vm_type = "default",
            persistent_disk_type = "10240",
            networks = arrayOf("default"),
            instances = 1,
            azs = arrayOf("z1", "z2", "z3")
    )
    private val release = ServiceRelease(
            name = "neo4j",
            version = "dev.1",
            jobs = arrayOf("neo4j")
    )
    private val serviceDeployment = ServiceDeployment(
            deployment_name = "service-instance_1b42a2ea-cb9b-4dd0-8859-267048723a42",
            releases = arrayOf(release),
            stemcell = ServiceStemcell("ubuntu-trusty", "3445.11"))

    private val expectedManifestJob = ManifestJob("neo4j", "neo4j", properties = mapOf())
    private val expectedNetwork = ManifestNetwork("default")

    @Before
    fun before() {
        `when`(jobGenerator.generateJob("neo4j", serviceDeployment)).thenReturn(Either.Right(expectedManifestJob))
        `when`(networkGenerator.generateNetwork("default")).thenReturn(expectedNetwork)
    }

    @Test
    fun `generates an instance group`() {
        val result = subject.generateInstanceGroup(instanceGroup, serviceDeployment) as Either.Right<ManifestInstanceGroup>

        assertThat(result.value)
                .isEqualTo(ManifestInstanceGroup(
                        name = "neo4j",
                        azs = arrayOf("z1", "z2", "z3"),
                        instances = 1,
                        jobs = arrayOf(expectedManifestJob),
                        vm_type = "default",
                        stemcell = "default",
                        networks = arrayOf(expectedNetwork)))
    }

    @Test
    fun `does not generate an instance group when no jobs are associated with it`() {
        val wrongInstanceGroupName = "ohoh-wrong-group-name"
        val instanceGroup = PlanInstanceGroup(
                name = wrongInstanceGroupName,
                vm_type = "default",
                persistent_disk_type = "10240",
                networks = arrayOf("default"),
                instances = 1,
                azs = arrayOf("z1", "z2", "z3")
        )

        val result = subject.generateInstanceGroup(instanceGroup, serviceDeployment) as Either.Left<List<ManifestCommandError>>

        assertThat(result.value).containsExactly(JobNotFound(wrongInstanceGroupName))
    }

    @Test
    fun `does not generate an instance group when the job generator fails`() {
        val expectedError = ReleaseNotFound("neo4j")
        `when`(jobGenerator.generateJob("neo4j", serviceDeployment)).thenReturn(Either.Left(expectedError))

        val result = subject.generateInstanceGroup(instanceGroup, serviceDeployment) as Either.Left<List<ManifestCommandError>>

        assertThat(result.value).containsExactly(expectedError)
    }
}