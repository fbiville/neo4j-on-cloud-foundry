package org.neo4j.cloudfoundry.odb.adapter.command.generator

import java.util.UUID

class PasswordGenerator {
    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}