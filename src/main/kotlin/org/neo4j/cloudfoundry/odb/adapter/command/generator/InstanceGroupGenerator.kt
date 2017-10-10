package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.command.error.JobNotFound
import org.neo4j.cloudfoundry.odb.adapter.command.error.ManifestCommandError
import org.neo4j.cloudfoundry.odb.adapter.command.generator.GeneratorConfig.Companion.jobsByInstanceGroupName
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.PlanInstanceGroup
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestInstanceGroup
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestJob

class InstanceGroupGenerator(val jobGenerator: JobGenerator, val networkGenerator: NetworkGenerator) {
    fun generateInstanceGroup(planInstanceGroup: PlanInstanceGroup, serviceDeployment: ServiceDeployment): Either<List<ManifestCommandError>, ManifestInstanceGroup> {

        val jobNames = findJobNamesByInstanceGroupName(planInstanceGroup.name!!)
        return if (jobNames == null) {
            Either.Left(listOf(JobNotFound(planInstanceGroup.name)))
        } else {
            val manifestJobs = generateManifestJobs(jobNames, serviceDeployment)
            Either.lefts(manifestJobs) ?: Either.Right(createManifestInstanceGroup(planInstanceGroup, manifestJobs))
        }
    }

    private fun generateManifestJobs(jobNames: Array<String>, serviceDeployment: ServiceDeployment) =
            jobNames.map { jobGenerator.generateJob(it, serviceDeployment) }

    private fun findJobNamesByInstanceGroupName(name: String): Array<String>? = jobsByInstanceGroupName[name]

    private fun createManifestInstanceGroup(planInstanceGroup: PlanInstanceGroup, jobs: List<Either<ManifestCommandError, ManifestJob>>): ManifestInstanceGroup {
        return ManifestInstanceGroup(
                name = planInstanceGroup.name,
                azs = planInstanceGroup.azs,
                instances = planInstanceGroup.instances,
                jobs = Either.rightsArray(jobs),
                vm_type = planInstanceGroup.vm_type,
                networks = planInstanceGroup.networks!!.map(networkGenerator::generateNetwork).toTypedArray(),
                stemcell = GeneratorConfig.singleStemcellName)
    }

}