package org.neo4j.cloudfoundry.odb.adapter

import org.neo4j.cloudfoundry.odb.adapter.command.DashboardUrlCommand
import org.neo4j.cloudfoundry.odb.adapter.command.GenerateManifestCommand
import org.neo4j.cloudfoundry.odb.adapter.command.ServiceAdapterCommand
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

class App(private val generateManifestCommand: GenerateManifestCommand,
          private val dashboardUrlCommand: DashboardUrlCommand) {

    fun execute(args: Array<String>) {
        val commandLine = CommandLine(MainCommand())
                .addSubcommand("generate-manifest", generateManifestCommand)
                .addSubcommand("dashboard-url", dashboardUrlCommand)
                .registerConverter(Manifest::class.java, ManifestConverter())
                .registerConverter(RequestParameters::class.java, RequestParametersConverter())
                .registerConverter(Plan::class.java, PlanConverter())
                .registerConverter(ServiceDeployment::class.java, ServiceDeploymentConverter())

        val commands = commandLine.parse(*args)

        val output = (commands.last().command as ServiceAdapterCommand).execute()
        when (output) {
            is CommandOutput.Standard -> {
                print(output.content)
                System.exit(successStatus)
            }
            is CommandOutput.Error -> {
                System.err.print(output.errorMessage)
                System.exit(errorStatus)
            }
            is CommandOutput.Unsupported -> {
                System.err.print(output.warningMessage)
                System.exit(warningStatus)
            }
        }
    }

    companion object {
        val successStatus = 0
        val errorStatus = 42
        val warningStatus = 10
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
    val dashboardUrlCommand = DashboardUrlCommand()

    App(generateManifest, dashboardUrlCommand).execute(args)
}
