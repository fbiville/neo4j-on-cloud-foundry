package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.command.error.ManifestCommandError
import org.neo4j.cloudfoundry.odb.adapter.command.error.ReleaseNotFound
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceRelease
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestJob

class JobGenerator {
    fun generateJob(job: String, serviceDeployment: ServiceDeployment): Either<ManifestCommandError, ManifestJob> {
        val release = this.findReleaseByJobName(job, serviceDeployment)
        if (release == null) {
            return Either.Left(ReleaseNotFound(job))
        }
        return Either.Right(ManifestJob(job, release.name!!, properties = mapOf()))
    }

    private fun findReleaseByJobName(jobName: String, serviceDeployment: ServiceDeployment): ServiceRelease? {
        return serviceDeployment.releases!!.find { release -> release.jobs!!.contains(jobName) }
    }
}