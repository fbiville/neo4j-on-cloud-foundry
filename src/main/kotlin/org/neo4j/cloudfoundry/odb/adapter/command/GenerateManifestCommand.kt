package org.neo4j.cloudfoundry.odb.adapter.command

import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.command.error.ManifestCommandError
import org.neo4j.cloudfoundry.odb.adapter.command.generator.ManifestGenerator
import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.domain.RequestParameters
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.Plan
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import org.neo4j.cloudfoundry.odb.adapter.serializer.YamlSerializer
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "generate-manifest")
class GenerateManifestCommand(val manifestGenerator: ManifestGenerator,
                              val yamlSerializer: YamlSerializer) {


    @Parameters(index = "0", arity = "1")
    lateinit var serviceDeployment: ServiceDeployment

    @Parameters(index = "1", arity = "1")
    lateinit var plan: Plan

    @Parameters(index = "2", arity = "1")
    lateinit var requestParams: RequestParameters

    @Parameters(index = "3", arity = "0..1")
    var previousManifest: Manifest? = null

    @Parameters(index = "4", arity = "0..1")
    var previousPlan: Plan? = null


    fun execute(): CommandOutput {

        val manifest = manifestGenerator.generateManifest(
                serviceDeployment,
                plan,
                requestParams.map,
                previousManifest,
                previousPlan
        )

        return when (manifest) {
            is Either.Left<List<ManifestCommandError>> -> CommandOutput.Error(concatErrors(manifest.value))
            is Either.Right<Manifest> -> CommandOutput.Standard(yamlSerializer.serialize(manifest.value))
        }

    }

    private fun concatErrors(values: List<ManifestCommandError>): String {
        return values
                .map { it.getDescription() }
                .reduce { acc, message ->
                    """$acc
                      |$message""".trimMargin()
                }
    }
}