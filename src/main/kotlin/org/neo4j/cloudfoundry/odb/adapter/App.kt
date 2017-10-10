package org.neo4j.cloudfoundry.odb.adapter

import org.neo4j.cloudfoundry.odb.adapter.command.GenerateManifestCommand
import org.neo4j.cloudfoundry.odb.adapter.command.converter.ManifestConverter
import org.neo4j.cloudfoundry.odb.adapter.command.converter.PlanConverter
import org.neo4j.cloudfoundry.odb.adapter.command.converter.RequestParametersConverter
import org.neo4j.cloudfoundry.odb.adapter.command.converter.ServiceDeploymentConverter
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.command.generator.InstanceGroupGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.JobGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.ManifestGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.NetworkGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.ReleaseGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.StemcellGenerator
import org.neo4j.cloudfoundry.odb.adapter.domain.RequestParameters
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.Plan
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import org.neo4j.cloudfoundry.odb.adapter.serializer.YamlSerializer
import picocli.CommandLine
import picocli.CommandLine.Command

@Command
class MainCommand

class App(private val generateManifestCommand: GenerateManifestCommand) {

    fun execute(args: Array<String>) {
        val commandLine = CommandLine(MainCommand())
                .addSubcommand("generate-manifest", generateManifestCommand)
                .registerConverter(Manifest::class.java, ManifestConverter())
                .registerConverter(RequestParameters::class.java, RequestParametersConverter())
                .registerConverter(Plan::class.java, PlanConverter())
                .registerConverter(ServiceDeployment::class.java, ServiceDeploymentConverter())

        val commands = commandLine.parse(*args)

        val output = (commands.last().command as GenerateManifestCommand).execute()
        when (output) {
            is CommandOutput.Standard -> {
                print(output.content)
                System.exit(successStatus)
            }
            is CommandOutput.Error -> {
                System.err.print(output.errorMessage)
                System.exit(errorStatus)
            }
        }
    }

    companion object {
        val successStatus = 0
        val errorStatus = 42
        val deserializationError = { name: String -> "Parameter '$name' cannot be deserialized" }
        val missingMandatoryFieldsError = { name: String, missing: List<String> -> "Parameter '$name' is missing mandatory parameters: ${missing.joinToString(", ")}" }
        val packageToScanForMissingFields = "org.neo4j.cloudfoundry.odb.adapter"
    }
}

fun main(args: Array<String>) {
    val generateManifest = GenerateManifestCommand(
            ManifestGenerator(
                    InstanceGroupGenerator(
                            JobGenerator(),
                            NetworkGenerator()
                    ),
                    StemcellGenerator(),
                    ReleaseGenerator()
            ),
            YamlSerializer()
    )

    App(generateManifest).execute(args)
}
