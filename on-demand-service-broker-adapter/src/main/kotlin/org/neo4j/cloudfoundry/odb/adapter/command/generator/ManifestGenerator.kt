package org.neo4j.cloudfoundry.odb.adapter.command.generator

import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.command.error.ManifestCommandError
import org.neo4j.cloudfoundry.odb.adapter.command.error.MigrationNotSupported
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.PlanInstanceGroup
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.Plan
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestInstanceGroup
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestProperties

class ManifestGenerator(val instanceGroupGenerator: InstanceGroupGenerator,
                        val stemcellGenerator: StemcellGenerator,
                        val releaseGenerator: ReleaseGenerator,
                        val passwordGenerator: PasswordGenerator) {

    fun generateManifest(serviceDeployment: ServiceDeployment,
                         plan: Plan,
                         requestParams: Map<String, String>,
                         previousManifest: Manifest? = null,
                         previousPlan: Plan? = null): Either<List<ManifestCommandError>, Manifest> {

        if (previousManifest != null || previousPlan != null) {
            return Either.Left(listOf(MigrationNotSupported))
        }

        val instanceGroups = plan.instance_groups!!.map(this.generateInstanceGroupLambda(serviceDeployment))
        val wrongInstanceGroups = Either.flattenLefts(instanceGroups)
        if (wrongInstanceGroups != null) {
            return wrongInstanceGroups
        }

        return Either.Right(Manifest(
                name = serviceDeployment.deployment_name,
                releases = serviceDeployment.releases!!.map(releaseGenerator::generateRelease).toTypedArray(),
                stemcells = arrayOf(stemcellGenerator.generateStemcell(serviceDeployment.stemcell!!)),
                update = plan.update, //TODO: provide default
                instance_groups = Either.rightsArray(instanceGroups),
                properties = ManifestProperties(passwordGenerator.generate()))
        )
    }

    private fun generateInstanceGroupLambda(serviceDeployment: ServiceDeployment):
            (PlanInstanceGroup) -> Either<List<ManifestCommandError>, ManifestInstanceGroup> {

        return {
            instanceGroupGenerator.generateInstanceGroup(it, serviceDeployment)
        }
    }
}

