package org.neo4j.cloudfoundry.odb.adapter.command.generator

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.*
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.command.error.ManifestCommandError
import org.neo4j.cloudfoundry.odb.adapter.command.error.ReleaseNotFound
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.JobProperties
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceRelease
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceStemcell
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestJob

class JobGeneratorTest {
    val passwordGenerator = mock<PasswordGenerator>()
    val subject = JobGenerator(passwordGenerator)

    @Test
    fun `generates a ManifestJob when the job is found in the release`() {
        val release = ServiceRelease(
                name = "neo4j",
                version = "dev.1",
                jobs = arrayOf("neo4j")
        )
        val serviceDeployment = ServiceDeployment(
                deployment_name = "service-instance_1b42a2ea-cb9b-4dd0-8859-267048723a42",
                releases = arrayOf(release),
                stemcell = ServiceStemcell("ubuntu-trusty", "3445.11"))

        whenever(passwordGenerator.generate()).thenReturn("beautiful-admin-p@ssw0rd")

        val result = subject.generateJob("neo4j", serviceDeployment) as Either.Right<ManifestJob>

        assertThat(result.value).isEqualTo(ManifestJob("neo4j", "neo4j", properties = JobProperties("beautiful-admin-p@ssw0rd")))
    }

    @Test
    fun `generates an error when no releases match the expected job`() {
        val wrongJobName = "neo5j"
        val jobToFind = "neo4j"
        val release = ServiceRelease(
                name = "neo4j",
                version = "dev.1",
                jobs = arrayOf(wrongJobName)
        )
        val serviceDeployment = ServiceDeployment(
                deployment_name = "service-instance_1b42a2ea-cb9b-4dd0-8859-267048723a42",
                releases = arrayOf(release),
                stemcell = ServiceStemcell("ubuntu-trusty", "3445.11"))


        val result = subject.generateJob(jobToFind, serviceDeployment) as Either.Left<ManifestCommandError>

        assertThat(result.value).isEqualTo(ReleaseNotFound(jobToFind))
    }
}
