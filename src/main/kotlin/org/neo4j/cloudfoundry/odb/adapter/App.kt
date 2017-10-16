package org.neo4j.cloudfoundry.odb.adapter

import com.google.gson.Gson
import org.neo4j.cloudfoundry.odb.adapter.command.CreateBindingCommand
import org.neo4j.cloudfoundry.odb.adapter.command.DashboardUrlCommand
import org.neo4j.cloudfoundry.odb.adapter.command.DeleteBindingCommand
import org.neo4j.cloudfoundry.odb.adapter.command.GenerateManifestCommand
import org.neo4j.cloudfoundry.odb.adapter.command.ServiceAdapterCommand
import org.neo4j.cloudfoundry.odb.adapter.command.converter.BoshVmConverter
import org.neo4j.cloudfoundry.odb.adapter.command.converter.MandatoryFieldsValidator
import org.neo4j.cloudfoundry.odb.adapter.command.converter.ManifestConverter
import org.neo4j.cloudfoundry.odb.adapter.command.converter.PlanConverter
import org.neo4j.cloudfoundry.odb.adapter.command.converter.RequestParametersConverter
import org.neo4j.cloudfoundry.odb.adapter.command.converter.ServiceDeploymentConverter
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.command.generator.InstanceGroupGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.JobGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.ManifestGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.NetworkGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.PasswordGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.ReleaseGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.generator.StemcellGenerator
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.CredentialsRepository
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.AdminPasswordSupplier
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.BoltUriSupplier
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.DriverSupplier
import org.neo4j.cloudfoundry.odb.adapter.domain.BoshVms
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
          private val dashboardUrlCommand: DashboardUrlCommand,
          private val createBindingCommand: CreateBindingCommand,
          private val deleteBindingCommand: DeleteBindingCommand,
          private val gson: Gson,
          private val yamlSerializer: YamlSerializer) {

    fun execute(args: Array<String>) {
        val mandatoryFieldsValidator = MandatoryFieldsValidator()
        val commandLine = CommandLine(MainCommand())
                .addSubcommand("generate-manifest", generateManifestCommand)
                .addSubcommand("dashboard-url", dashboardUrlCommand)
                .addSubcommand("create-binding", createBindingCommand)
                .addSubcommand("remove-binding", deleteBindingCommand)
                .registerConverter(Manifest::class.java, ManifestConverter(yamlSerializer, mandatoryFieldsValidator))
                .registerConverter(RequestParameters::class.java, RequestParametersConverter(gson))
                .registerConverter(Plan::class.java, PlanConverter(gson, mandatoryFieldsValidator))
                .registerConverter(ServiceDeployment::class.java, ServiceDeploymentConverter(gson, mandatoryFieldsValidator))
                .registerConverter(BoshVms::class.java, BoshVmConverter(gson, mandatoryFieldsValidator))

        val commands = commandLine.parse(*args)

        val output = (commands.last().command as ServiceAdapterCommand).execute()
        when (output) {
            is CommandOutput.Standard -> {
                print(output.content)
                System.exit(0)
            }
            is CommandOutput.Error -> {
                System.err.print(output.errorMessage)
                System.exit(output.errorStatus)
            }
            is CommandOutput.Unsupported -> {
                System.err.print(output.warningMessage)
                System.exit(10)
            }
        }
    }
}


fun main(args: Array<String>) {
    val gson = Gson()
    val yamlSerializer = YamlSerializer()

    val passwordGenerator = PasswordGenerator()

    val generateManifestCommand = GenerateManifestCommand(
            ManifestGenerator(
                    InstanceGroupGenerator(
                            JobGenerator(),
                            NetworkGenerator()
                    ),
                    StemcellGenerator(),
                    ReleaseGenerator(),
                    passwordGenerator //TODO: admin password should be defined in the Bosh release??
            ),
            yamlSerializer
    )
    val dashboardUrlCommand = DashboardUrlCommand()
    val credentialsRepository = CredentialsRepository(passwordGenerator)
    val driverSupplier = DriverSupplier()
    val boltUriSupplier = BoltUriSupplier()
    val adminPasswordSupplier = AdminPasswordSupplier()
    val createBindingCommand = CreateBindingCommand(
            credentialsRepository,
            gson,
            driverSupplier,
            boltUriSupplier,
            adminPasswordSupplier)
    val deleteBindingCommand = DeleteBindingCommand(
            credentialsRepository,
            driverSupplier,
            boltUriSupplier,
            adminPasswordSupplier
    )
    App(generateManifestCommand,
        dashboardUrlCommand,
        createBindingCommand,
        deleteBindingCommand,
        gson,
        yamlSerializer
    ).execute(args)
}
