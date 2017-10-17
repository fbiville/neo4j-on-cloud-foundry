package org.neo4j.cloudfoundry.odb.adapter.command

import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput

interface ServiceAdapterCommand {
    fun execute(): CommandOutput
}