package org.neo4j.cloudfoundry.odb.adapter.command.error

sealed class CommandOutput {
    data class Standard(val content: String): CommandOutput()
    data class Error(val errorStatus: Int, val errorMessage: String): CommandOutput()
    data class Unsupported(val warningMessage: String): CommandOutput()
}