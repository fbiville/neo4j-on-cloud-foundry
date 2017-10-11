package org.neo4j.cloudfoundry.odb.adapter.command

import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.Plan
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(name = "dashboard-url")
class DashboardUrlCommand : ServiceAdapterCommand {

    @CommandLine.Parameters(index = "0", arity = "1")
    lateinit var bindingId: String

    @CommandLine.Parameters(index = "1", arity = "1")
    lateinit var plan: Plan

    @CommandLine.Parameters(index = "2", arity = "1")
    lateinit var manifest: Manifest


    override fun execute(): CommandOutput {
        return CommandOutput.Unsupported("Command is not supported")
    }

}