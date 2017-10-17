package org.neo4j.cloudfoundry.odb.adapter.command.persistence

class PersistenceError(message: String, cause: Exception): Exception(message, cause)