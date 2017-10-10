package org.neo4j.cloudfoundry.odb.adapter.command.error

sealed class CommandOutput {
    data class Error(val errorMessage: String): CommandOutput()
    data class Standard(val content: String): CommandOutput()
}